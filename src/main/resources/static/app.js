var app = angular.module('App', ["ui.bootstrap", "ui.router", "ngRoute", "ngAlertify", "ngDialog", "ngSanitize", 'auth0', 'angular-storage', 'angular-jwt']);

app.constant('API', 'http://test-routes.herokuapp.com');


app.controller('MainCtrl', function ($scope) {
    $scope.tabs = [
        {title: "Orders", route: "orders"},
        {title: "Invoices", route: "invoices"}
    ];
});


app.config(function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, jwtInterceptorProvider, authProvider) {

    $stateProvider.state('login', {
        url: '/login',
        tempalteUrl: 'view-login.html',
        controller: 'LoginCtrl'
    });

    $stateProvider.state('orders', {
        url: "/orders",
        templateUrl: "view-orders.html",
        controller: 'OrderCtrl',
        data: {
            requiresLogin: true
        }
    });

    $stateProvider.state('invoices', {
        url: "/invoices",
        templateUrl: "view-invoices.html",
        controller: 'InvoiceCtrl',
        data: {
            requiresLogin: true
        }
    });

    $urlRouterProvider.otherwise('orders');

    authProvider.init({
        domain: 'craftsmans.eu.auth0.com',
        clientID: 'N3fXLU6Vvi6FFlVzBE8W6fg6ujHXEi1M',
        loginState: 'login'
    });

    jwtInterceptorProvider.tokenGetter = function (store) {
        console.log(store.get('token'));
        return store.get('token');
    };

    $httpProvider.interceptors.push('jwtInterceptor');


    // function redirect($q, $injector, $timeout, store, $location) {
    //
    //     var auth;
    //     $timeout(function() {
    //         auth = $injector.get('auth');
    //     });
    //
    //     return {
    //         responseError: function(rejection) {
    //
    //             if (rejection.status === 401) {
    //                 auth.signout();
    //                 store.remove('profile');
    //                 store.remove('token');
    //                 $location.path('/home')
    //             }
    //             return $q.reject(rejection);
    //         }
    //     }
    // }
    // $provide.factory('redirect', redirect);
    // $httpProvider.interceptors.push('redirect');

});

app.run(function ($rootScope, $state, auth, store, jwtHelper) {
    auth.hookEvents();
    //  the isAuthenticated boolean is not persisted, so we re-set it during page refresh
    $rootScope.$on('$locationChangeStart', function () {
            var token = store.get('token');
            var profile = store.get('profile');
            if (token) {
                if (!jwtHelper.isTokenExpired(token) && !auth.isAuthenticated) {
                    auth.authenticate(profile, token);
                }
            }
        }
    );
});


app.controller('LoginCtrl', function ($state, auth, store) {
    auth.signin({popup: true, closable: false}, function (profile, token) {
        store.set('profile', profile);
        store.set('token', token);
        $state.go("orders");
    }, function (error) {
        console.log(error);
    })
});


app.controller('AuthCtrl', function ($scope, $location, $state, auth, store) {
    $scope.auth = auth;

    $scope.login = function () {
        auth.signin({popup: true, closable: false}, function (profile, token) {
            store.set('profile', profile);
            store.set('token', token);
            $state.go("orders")
        }, function (error) {
            console.log(error);
        })
    };

    $scope.logout = function () {
        auth.signout();
        store.remove('profile');
        store.remove('token');
        $state.reload();
    }

});


app.controller('CreateEmailPopupCtrl', function ($scope, $uibModalInstance, email) {

    $scope.email = email;

    $scope.ok = function () {
        $uibModalInstance.close(email);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});


app.controller('CreateInvoicePopupCtrl', function ($scope, $uibModalInstance) {

    $scope.ok = function () {
        $uibModalInstance.close($scope.invoice);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});

