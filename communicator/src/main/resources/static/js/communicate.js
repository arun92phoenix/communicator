/**
 * 
 */
var app = angular.module('Communicator', []);

app.service('Notifier', function() {
	if (Notification.permission !== "granted")
		Notification.requestPermission();

	this.notify = function(from, message) {
		if (Notification.permission == "granted") {
			var notification = new Notification(from, {
				body : message
			});

			notification.onclick = function() {
				alert('Closed');
			};
		}
	}
});

app.controller('MainCtrl', [ '$scope', 'Notifier', function($scope, Notifier) {
	var stompClient = Stomp.over(new SockJS('/communicate'));

	$scope.messages = [];
	$scope.participants = [];

	function updateParticipants(message) {
		var participants = JSON.parse(message.body);

		$scope.$evalAsync(function() {
			$scope.participants = participants;
		});
	}

	stompClient.connect({}, function() {
		stompClient.subscribe('/user/receive', function(message) {
			message = JSON.parse(message.body);
			Notifier.notify(message.from, message.message);
			$scope.$evalAsync(function() {
				$scope.messages.push(message);
			});
		});

		stompClient.subscribe('/update.participants', function(message) {
			updateParticipants(message);
		});

		stompClient.subscribe('/participants', function(message) {
			updateParticipants(message);
		})
	});

	$scope.sendMessage = function() {
		var message = {
			'message' : $scope.message
		};

		stompClient.send('/send.to.' + $scope.to, {}, JSON.stringify(message));
		$scope.message = '';
	}
} ]);