var app = angular.module('Communicator', []);

app.controller('LoginCtrl', [ '$scope', function($scope) {
	if (location.search.indexOf('error') != -1) {
		$scope.showError = true;
	}
} ]);