angular.module('App').controller('OrderCtrl', function ($scope, $http, $location, $q, alertify, ngDialog, firebase) {

    alertify.closeLogOnClick(true);
    alertify.delay(10000);
    alertify.logPosition("top right");

    function momentize(date) {
        return moment(
            [date.year, date.monthValue - 1, date.dayOfMonth]
        )
    }


    function ticketLinks(orderRef) {
        return firebase('ticketLinks' + '/' + orderRef + "/links");
    }

    function anInvoice(orderRef) {
        return firebase("invoices", function (root) {
            return root.orderByChild("orderRef").equalTo(orderRef);
        });
    }

    firebase('orders')
        .then(function (it) {
            $scope.orders = it;
            $scope.keys = Object.keys(it);
            $scope.keys.forEach(function (orderRef, index) {
                ticketLinks(orderRef)
                    .then(function (links) {
                        $scope.orders[orderRef].links = links;
                    });
                anInvoice(orderRef)
                    .then(function (invoice) {
                        if (invoice) {
                            var documentNumber = Object.keys(invoice)[0];
                            $scope.orders[orderRef].anInvoice = invoice[documentNumber];
                            $scope.orders[orderRef].delayed = momentize(invoice[documentNumber].dueAt).isBefore(moment().valueOf());
                        }

                        var hasInvoice = $scope.orders[orderRef].invoice && !$scope.orders[orderRef].invoice.legacy;
                        if (hasInvoice) {
//                                                var documentNumber = $scope.orders[orderRef].invoice.documentNumber;
                        }

                    });

            });
        });


    $scope.generateInvoice = function (index) {
        var key = $scope.keys[index];
        ngDialog.open({
            template: 'invoice-for-order.html',
            className: 'ngdialog-theme-default',
            controller: ['$scope', function ($popup) {
                $popup.generateInvoiceWithVat = function (vat) {
                    $http.post("/orders/" + key + "/invoices/" + vat, {})
                        .success(function () {
                            alertify.success("OKI");
                        })
                        .error(function () {
                            alertify.error("Fuckup");
                        })
                }
            }]
        });
    };

    // $scope.generateInvoice = function (index) {
    //     var key = $scope.keys[index];
    //     $http.post("/orders/" + key + "/invoices", {})
    //         .success(function () {
    //             alertify.success("OKI");
    //         })
    //         .error(function () {
    //             alertify.error("Fuckup");
    //         })
    //
    // };

    $scope.generateTicket = function (index) {
        var key = $scope.keys[index];
        $http.post("/orders/" + key + "/tickets", {})
            .success(function () {

            })
            .error(function () {

            })
    };

    $scope.sendInvoice = function (index) {
        var key = $scope.keys[index];
        ngDialog.open({
            template: 'email.html',
            className: 'ngdialog-theme-default',
            controller: ['$scope', function ($popup) {
                $popup.send = function (test) {
                    $http.post("/invoices/" + key + "/send/" + test)
                        .success(function () {
                            alertify.success("OK!");
                        })
                        .error(function () {
                            alertify.error("NOK");
                        })
                }
            }]
        });
    };


    $scope.setStatus = function (index, status) {
        var key = $scope.keys[index];
        $scope.orders[key].status = status;
        $http.post("/orders/" + key + "/status/" + status)
            .success(function () {
                alertify.success("OK!");
            })
            .error(function () {
                alertify.error("NOK");
            })
    };


    $scope.sendTickets = function (index) {
        var key = $scope.keys[index];
        ngDialog.open({
            template: 'tickets.html',
            className: 'ngdialog-theme-default',
            controller: ['$scope', function ($popup) {
                $popup.sendTickets = function (test) {
                    $http.post("/tickets/" + key + "/send/" + test)
                        .success(function () {
                            alertify.success("OK!");
                        })
                        .error(function () {
                            alertify.error("NOK");
                        })
                }
            }]
        });
    };

});