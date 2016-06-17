/**
 * 
 */
var app = angular.module('Communicator', []);

app.controller('MainCtrl', [ '$scope', '$timeout', function($scope, $timeout) {
	var stompClient = Stomp.over(new SockJS('/communicate'));

	$scope.messages = [];
	$scope.participants = [];

	stompClient.connect({}, function() {
		stompClient.subscribe('/user/receive', function(message) {
			message = JSON.parse(message.body);
			$timeout(function() {
				$scope.messages.push(message);
			});

		});

		stompClient.subscribe('/participants', function(message) {
			$timeout(function(){
				$scope.participants = JSON.parse(message.body);
			});
		});
	});

	$scope.sendMessage = function() {
		var message = {
			'message' : $scope.message
		};
		stompClient.send('/send.to.' + $scope.to, {}, JSON.stringify(message));
		$scope.message = '';
	}
} ]);