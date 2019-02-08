angular.module('app.service', []).factory('CustomerService', ['$http', function ($http) {

    var urlBase = 'http://localhost:8080/api';
    var serviceApi = {};

    serviceApi.getCustomers = function (user) {
        return $http.post(urlBase+'/GetCustomers', user);
    };

    return serviceApi;

}]);