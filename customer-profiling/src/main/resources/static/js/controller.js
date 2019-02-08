var app = angular.module('app.controller', []);
app.controller('controller', function($scope, $filter , CustomerService) {
	$scope.user = {};
	$scope.months = [ 'January' ,'February' ,'March','April','May' ,'June' , 'July', 'August','September','October','November' ,'December'];
	$scope.years = [];

	for (var x = 2019; x > 2000; x--) {
	    $scope.years.push(x);
	}

	$scope.user.selectedYear = $scope.years[0];
	$scope.user.selectedMonth= $scope.months[0];
	
	$scope.submit = function(){
	  searchCustomer($scope.user);
    }
	
	function searchCustomer(user) {
        $scope.user.monthlyTramsactionList = '';
        CustomerService.getCustomers(user)
            .then(
                function (response) {
                	console.log(response);
                	$scope.user.data = response.data;
                	$scope.user.monthlyTramsactionList = response.data.monthlyTramsactionList;
                	if($scope.user.monthlyTramsactionList != null && $scope.user.monthlyTramsactionList != ''){
                		for(var i= 0 ; i < $scope.user.monthlyTramsactionList.length ; i++)
                			$scope.user.monthlyTramsactionList[i].dateTimeOfTransaction = $filter('date')($scope.user.monthlyTramsactionList[i].dateTimeOfTransaction ,'yyyy-MM-dd HH:mm:ss');
                	}
                	
                	var classificationSet = $scope.user.data.classificationSet;
                	if(classificationSet != null){
                		$scope.user.classify = '';
                		for(var i= 0 ; i < classificationSet.length ; i++)
                			$scope.user.classify = 	$scope.user.classify + classificationSet[i] + '/';
                		$scope.user.classify  = $scope.user.classify .substring(0 , $scope.user.classify.length -1);   
                	}
                	
                },
                function (errResponse) {
                	console.log(errResponse);
                    console.error('Error while searching customer');
                    
                }
            );
    }
});