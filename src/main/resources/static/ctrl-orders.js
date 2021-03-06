angular.module('App').controller('OrderCtrl', function ($scope, $http, $location, $q, alertify, ngDialog, firebase, $uibModal) {

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

            var ordersByProduct = _.groupBy($scope.orders, 'productName');
            $scope.products = _.keys(ordersByProduct);


            _.each($scope.products, function (v) {
                $scope[v] = _.sumBy(ordersByProduct[v], function (o) {
                    if (_.isNumber(o.reservations)) {
                        return o.reservations;
                    } else {
                        return _.size(o.tickets);
                    }
                });
            });


            // {{order.tickets | children}} {{order.reservations}}

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

    $scope.participants = function (index) {
        var order = $scope.keys[index];
        var popup = $uibModal.open({
            templateUrl: 'participants.html',
            controller: 'ParticipantsPopupCtrl',
            resolve: {
                participants: function () {
                    return $scope.orders[order].tickets;
                }
            }
        });

        popup.result.then(function (newParticipant) {
            $http.post("/orders/" + order + "/participants", newParticipant)
                .success(function () {
                    alertify.success("OK!");
                })
                .error(function () {
                    alertify.error("NOK");
                })
        });
    };


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

