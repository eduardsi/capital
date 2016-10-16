angular.module('App').controller('InvoiceCtrl', function ($rootScope, $scope, $state, $uibModal, $log, $http, alertify, auth, firebase) {

    firebase('invoices').then(function (it) {
        $scope.invoices = _.sortBy(it, [function (o) {
            var orderNo = _.split(o.documentNumber, '-', 2)[1];
            return _.toNumber(orderNo);
        }]);
    });

    $scope.open = function (size) {

        var popup = $uibModal.open({
            templateUrl: 'myModalContent.html',
            controller: 'CreateInvoicePopupCtrl',
            size: size
        });

        popup.result.then(function (invoice) {
            $http.post("/invoices", invoice)
                .success(function () {
                    alertify.success("OKI");
                })
                .error(function () {
                    alertify.error("Fuckup");
                })

        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };


    $scope.invoiceTo = function (invoice) {
        var popup = $uibModal.open({
            templateUrl: 'createEmail.html',
            controller: 'CreateEmailPopupCtrl',
            size: 'lg',
            resolve: {
                email: function () {
                    return {to: invoice.recipient.emailAddress, documentNumber: invoice.documentNumber}
                }
            }
        });
        popup.result.then(function (email) {
            $http.post("/email/" + email.to + "/" + "eduards@sizovs.net" + "/" + email.documentNumber, email.body)
                .success(function () {
                    alertify.success("OKI");
                })
                .error(function () {
                    alertify.error("Fuckup");
                })

        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });

    }

});