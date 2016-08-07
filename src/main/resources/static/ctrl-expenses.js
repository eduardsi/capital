angular.module('App').controller('ExpenseCtrl', function ($scope, $uibModal, firebase) {
    
    firebase('expenses').then(function (it) {
        $scope.expenses = it;

        $scope.preview = function (url) {
            var popup = $uibModal.open({
                templateUrl: 'view-pdf.html',
                controller: 'ShowInvoiceCtrl',
                size: 'lg',
                resolve: {
                    url: function () {
                        return url;
                    }
                }
            });
        }
        
    });
});


angular.module('App').controller('ShowInvoiceCtrl', function ($scope, $uibModalInstance, url) {

    $scope.pdfUrl = url;

    $scope.ok = function () {
        $uibModalInstance.close();
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});