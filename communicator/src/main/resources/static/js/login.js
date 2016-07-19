var app = angular.module('Communicator', []);

app.controller('LoginCtrl', [ '$scope', function($scope) {
	debugger;
	if (location.search.indexOf('error') != -1) {
		$scope.showError = true;
	}
} ]);