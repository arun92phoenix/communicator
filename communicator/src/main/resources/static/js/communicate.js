/**
 * 
 */
var app = angular.module('Communicator', []);

app.service('Notifier', function() {
	var isWindowActive = true;

	if (Notification.permission !== "granted")
		Notification.requestPermission();

	window.onfocus = function() {
		isWindowActive = true;
	}

	window.onblur = function() {
		isWindowActive = false;
	}

	this.notify = function(from, message, closeHandler) {
		if (isWindowActive) {
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
									'own' : true
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
								$timeout(function() {
									document.getElementById('chat-input')
											.focus();
								});

							}

							$scope.addToMessages = function(message,
									participant) {
								message.time = new Date();
								if (!$scope.messages[participant]) {
									$scope.messages[participant] = [];
								}

								if ($scope.selectedParticipant != participant) {

									if (!$scope.unreadCount[participant]) {
										$scope.unreadCount[participant] = 0;
									}

									$scope.unreadCount[participant]++;
								}

								$scope.messages[participant].push(message);

								// Letting the messages bind and show before
								// adjusting the
								// scroll height
								$timeout(function() {
									messagesBox.scrollTop = messagesBox.scrollHeight;
								});

							}
						} ]);