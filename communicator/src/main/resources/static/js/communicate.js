/**
 * 
 */
var isWindowActive = true;
var isNotificationAvailable = false;

window.onfocus = function () {
	isWindowActive = true;
}

window.onblur = function () {
	isWindowActive = false;
}

var app = angular.module('Communicator', ['ngSanitize']);

app.service('Notifier', function () {

	if (Notification) {
		isNotificationAvailable = true;
	}

	if (isNotificationAvailable && Notification.permission !== "granted")
		Notification.requestPermission();

	this.notify = function (from, message, closeHandler) {
		if (isWindowActive || !isNotificationAvailable) {
			return;
		}

		if (Notification.permission == "granted") {
			var notification = new Notification(from, {
				body: message
			});

			notification.onclick = function () {
				closeHandler(from);
				this.close();
			};
		}
	}
});

app.filter('highlight', function () {
	return function (input, highlight) {
		var retStr = input;
		if (highlight) {
			retStr = input.replace(new RegExp(highlight, 'g'), '<span class="highlight">' + highlight + '</span>');
		}

		return retStr;
	}
});

app.controller('MainCtrl', ['$scope', 'Notifier', '$timeout', '$http', function ($scope, Notifier, $timeout, $http) {
	var stompClient = Stomp.over(new SockJS('/communicate'));

	$scope.messages = {};
	$scope.unreadCount = {};
	$scope.participants = [];
	$scope.unreadMessageNotifications = {};
	$scope.window = 'PARTICIPANTS';

	$scope.updateParticipants = function () {
		$http.get('/users/list')
			.then(function (data) {
				$scope.participants = data.data;
			},
			function (error) {
				alert("Failed to get participant list. " + error.data.message);
			});
	}

	stompClient.connect({}, function () {
		stompClient.subscribe('/user/receive',
			function (message) {
				message = JSON.parse(message.body);

				Notifier.notify(message.messageFrom, message.messageBody, $scope.showChat);
				$scope.$evalAsync(function () {
					$scope.addToMessages(message, message.messageFrom);
				});
			});

	});

	$scope.sendMessage = function () {
		var message = {
			'messageBody': $scope.message,
			'messageTo' : $scope.selectedParticipant
		};

		stompClient.send('/send.to.' + $scope.selectedParticipant, {}, JSON.stringify(message));

		$scope.addToMessages(message, $scope.selectedParticipant)
		$scope.message = '';
		$scope.removeUnreadNotification($scope.selectedParticipant);
	}

	$scope.getMessages = function (last) {
		$http.get('/messages/' + $scope.selectedParticipant + '?last=' + last)
			.then(function (data) {
				if($scope.messages[$scope.selectedParticipant]){
					$scope.messages[$scope.selectedParticipant] = data.data.content.reverse().concat($scope.messages[$scope.selectedParticipant]);
					var tempScrollTop = document.getElementById('messages-box').scrollHeight;
					$timeout(function () {
						$('#messages-box').scrollTop(document.getElementById('messages-box').scrollHeight - tempScrollTop);
					});
				} else {
					$scope.messages[$scope.selectedParticipant] = data.data.content.reverse();
					
					$timeout(function () {
						$('#chat-input').focus();
						$scope.scrollToEnd();
					});
				}
			});
	}

	$scope.showChat = function (participant) {
		debugger;
		$scope.selectedParticipant = participant;

		if ($scope.messages[participant] == null
			|| $scope.messages[participant].length == 0) {
			$scope.getMessages(0);
		}

		$scope.unreadCount[participant] = 0;

		$timeout(function () {
			$scope.clearUnreadMessage(participant);
		}, 5000);
	}

	$scope.scrollToEnd = function () {
		$timeout(function () {
			$('#messages-box').scrollTop(document.getElementById('messages-box').scrollHeight);
		});
	};

	$scope.addToMessages = function (message, participant) {
		// Addding the message to the list
		message.messageTime = new Date();
		if (!$scope.messages[participant]) {
			$scope.messages[participant] = [];
		}

		// Moving the message to the top
		$scope.participants.splice($scope.participants.indexOf(participant), 1);
		$scope.participants.unshift(participant);

		// Updating the unread count
		if ($scope.selectedParticipant != participant || !isWindowActive) {

			if (!$scope.unreadCount[participant]) {
				$scope.unreadCount[participant] = 0;
			}

			$scope.unreadCount[participant]++;

			if ($scope.unreadMessageNotifications[participant] == null) {
				$scope.messages[participant].push({
					type: 'unread',
					messageBody: 'Unread Messages'
				});

				$scope.unreadMessageNotifications[participant] = $scope.messages[participant].length - 1;
				$scope.scrollToEnd();
			}

		}

		$scope.messages[participant].push(message);

		if ($scope.selectedParticipant == participant && isWindowActive) {
			$scope.scrollToEnd();
		}

	}

	$scope.$watch('unreadCount', function (n) {
		var totalUnreadCount = 0;
		for (p in n) {
			totalUnreadCount = totalUnreadCount + $scope.unreadCount[p];
		}

		if (totalUnreadCount != 0) {
			document.title = '(' + totalUnreadCount + ') Communicator';
		} else {
			document.title = 'Communicator';
		}
	}, true);

	$scope.getAlignment = function (message) {

		if(message.type=='unread'){
			return 'center';
		} else if(message.messageTo==$scope.selectedParticipant){
			return 'right';
		} else {
			return '';
		}
	}

	$scope.clearUnreadMessage = function (o) {
		if ($scope.unreadMessageNotifications[o] != null
			&& $scope.messages[o].length > $scope.unreadMessageNotifications[o]) {
			$scope.messages[o].splice($scope.unreadMessageNotifications[o], 1);
			delete $scope.unreadMessageNotifications[o];
		}
	}

	$scope.$watch('selectedParticipant', function (n, o) {
		if (!o) {
			return;
		}

		$scope.clearUnreadMessage(o);

	});

	$scope.addParticipant = function () {
		if ($scope.newuser.password != $scope.newuser.confirmPassword) {
			alert('New Password and Confirm New Password should match!');
			return;
		}
		$http.post('/users/add', $scope.newuser).then(
			function () {
				alert("User Added Successfully!");
				$scope.updateParticipants();
				$scope.window = 'PARTICIPANTS';
				$scope.newuser = {};

			},
			function (error) {
				alert("Failed to add user. " + error.data.message);
			});
	};

	$scope.changePassword = function () {
		if ($scope.changepass.newPassword != $scope.changepass.confirmNewPassword) {
			alert('New Password and Confirm New Password should match!');
			return;
		}

		$http.post('/users/changepassword', $scope.changepass)
			.then(function () {
				alert("Password Changed Successfully!");
				$scope.window = 'PARTICIPANTS';
				$scope.newpass = {};
			},
			function (error) {
				alert("Failed to change password. " + error.data.message);
			});
	};

	$scope.updateParticipants();

	$('#messages-box').scroll(function () {
		var pos = $('#messages-box').scrollTop();
		if (pos == 0 && $scope.messages[$scope.selectedParticipant]) {
			$scope.getMessages($scope.messages[$scope.selectedParticipant][0].id);
		}
	});
}]);