/*global define */

'use strict';

define(function() {

/* Controllers */

var controllers = {};

controllers.MyCtrl1 = function($scope) {

    $scope.login = function() {
        alert('this is scope speaking');
    }
}
controllers.MyCtrl1.$inject = ['$scope'];

controllers.MyCtrl2 = function() {

}
controllers.MyCtrl2.$inject = [];

controllers.LoginCtrl = function($scope, Config, User) {

    $scope.showLoginForm = true;

    /**
     * toggleForm - it switches between login and register form
     */
    $scope.toggleForm = function() {
        $scope.showLoginForm = ! $scope.showLoginForm;
    }

    /**
     * login - it is triggered when user clicks on login button
     */
    $scope.login = function() {
        //alert('we are trying to run the damn login function :: ' + Config.url);
        User.logIn($scope.username, $scope.password, function(status) {
            if (status == 200) {
                //the login was successful we can store the data in local cookie
                //then we should redirect to the other view where we show the user account
                alert(User.getUserName());
            }
            else {
                //something went wrong we should notify the user
                //username or password is wrong
                alert(status);
            }
        });
    }

    /**
    * register - it is triggered when user clicks on register button
    */
    $scope.register = function() {
        if ($scope.username != undefined && $scope.password != undefined && $scope.username.length > 0 && $scope.password.length > 0) {
            User.register($scope.username, $scope.password, function(response) {
                alert(response);
            });
        }
        else {
            return;
        }
    }

}

controllers.LoginCtrl.$inject = ['$scope','Config','User'];

return controllers;

});