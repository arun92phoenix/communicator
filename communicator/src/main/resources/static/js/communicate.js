/**
 * 
 */
var isWindowActive = true;
var isNotificationAvailable = false;

window.onfocus = function() {
	isWindowActive = true;
}

window.onblur = function() {
	isWindowActive = false;
}

var app = angular.module('Communicator', [ 'ngSanitize' ]);

app.service('Notifier', function() {

	if (Notification) {
		isNotificationAvailable = true;
	}

	if (isNotificationAvailable && Notification.permission !== "granted")
		Notification.requestPermission();

	this.notify = function(from, message, closeHandler) {
		if (isWindowActive || !isNotificationAvailable) {
			return;
		}

		if (Notification.permission == "granted") {
			var notification = new Notification(from, {
				body : message
			});

			notification.onclick = function() {
				closeHandler(from);
				this.close();
			};
		}
	}
});

app.filter('highlight', function() {
	return function(input, highlight) {
		var retStr = input;
		if (highlight) {
			retStr = input.replace(new RegExp(highlight, 'g'),
					'<span class="highlight">' + highlight + '</span>');
		}

		return retStr;

	}
});

app
		.controller(
				'MainCtrl',
				[
						'$scope',
						'Notifier',
						'$timeout',
						function($scope, Notifier, $timeout) {
							var stompClient = Stomp.over(new SockJS(
									'/communicate'));

							$scope.messages = {};
							$scope.unreadCount = {};
							$scope.participants = [];
							$scope.unreadMessageNotifications = {};

							var messagesBox = document
									.getElementById('messages-box');

							$scope.updateParticipants = function(message) {
								var participants = JSON.parse(message.body);

								$scope.$evalAsync(function() {
									$scope.participants = participants;
								});

								if ($scope.selectedParticipant
										&& participants
												.indexOf($scope.selectedParticipant) == -1) {
									$scope.selectedParticipant = null;
								}
							}

							stompClient.connect({}, function() {
								stompClient.subscribe('/user/receive',
										function(message) {
											message = JSON.parse(message.body);

											Notifier.notify(message.from,
													message.message,
													$scope.showChat);
											$scope.$evalAsync(function() {
												$scope.addToMessages(message,
														message.from);
											});
										});

								stompClient.subscribe('/update.participants',
										function(message) {
											$scope.updateParticipants(message);
										});

								stompClient.subscribe('/participants',
										function(message) {
											$scope.updateParticipants(message);
										})
							});

							$scope.sendMessage = function() {
								var message = {
									'message' : $scope.message,
									'type' : 'own'
								};

								stompClient.send('/send.to.'
										+ $scope.selectedParticipant, {}, JSON
										.stringify(message));

								$scope.addToMessages(message,
										$scope.selectedParticipant)
								$scope.message = '';
							}

							$scope.showChat = function(participant) {
								$scope.selectedParticipant = participant;
								$scope.unreadCount[participant] = 0;
								$scope.scrollToEnd();
								$timeout(function() {
									document.getElementById('chat-input')
											.focus();
								});

								$timeout(function() {
									$scope.clearUnreadMessage(participant);
								}, 5000);

							}

							$scope.scrollToEnd = function() {
								$timeout(function() {
									messagesBox.scrollTop = messagesBox.scrollHeight;
								});
							};

							$scope.addToMessages = function(message,
									participant) {

								// Addding the message to the list
								message.time = new Date();
								if (!$scope.messages[participant]) {
									$scope.messages[participant] = [];
								}

								// Moving the message to the top
								$scope.participants.splice($scope.participants
										.indexOf(participant), 1);
								$scope.participants.unshift(participant);

								// Updating the unread count
								if ($scope.selectedParticipant != participant
										|| !isWindowActive) {

									if (!$scope.unreadCount[participant]) {
										$scope.unreadCount[participant] = 0;
									}

									$scope.unreadCount[participant]++;

									if ($scope.unreadMessageNotifications[participant] == null) {
										$scope.messages[participant].push({
											type : 'unread',
											message : 'Unread Messages'
										});

										$scope.unreadMessageNotifications[participant] = $scope.messages[participant].length - 1;
										$scope.scrollToEnd();
									}

								}

								$scope.messages[participant].push(message);

								if ($scope.selectedParticipant == participant
										&& isWindowActive) {
									$scope.scrollToEnd();
								}

							}

							$scope.$watch('unreadCount', function(n) {
								var totalUnreadCount = 0;
								for (p in n) {
									totalUnreadCount = totalUnreadCount
											+ $scope.unreadCount[p];
								}

								if (totalUnreadCount != 0) {
									document.title = '(' + totalUnreadCount
											+ ') Communicator';
								} else {
									document.title = 'Communicator';
								}
							}, true);

							$scope.getAlignment = function(type) {

								switch (type) {
								case 'own':
									return 'right';
								case 'unread':
									return 'center';
								default:
									return '';
								}
							}

							$scope.clearUnreadMessage = function(o) {
								if ($scope.unreadMessageNotifications[o] != null
										&& $scope.messages[o].length > $scope.unreadMessageNotifications[o]) {
									$scope.messages[o]
											.splice(
													$scope.unreadMessageNotifications[o],
													1);
									delete $scope.unreadMessageNotifications[o];
								}
							}

							$scope.$watch('selectedParticipant',
									function(n, o) {
										if (!o) {
											return;
										}
										$scpoe.clearUnreadMessage(o);

									});

						} ]);