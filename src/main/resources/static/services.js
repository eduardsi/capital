angular.module('App').factory('firebase', ['$q', function ($q) {

    var config = {
        apiKey: "AIzaSyAWD1zzN23OTchDyQhH462xJ8kW30_j0uU",
        authDomain: "money-edc4a.firebaseapp.com",
        databaseURL: "https://money-edc4a.firebaseio.com",
        storageBucket: ""
    };

    firebase.initializeApp(config);

    return function (ref, navi) {
        if (!navi) {
            navi = function (root) {
                return root;
            };
        }
        var def = $q.defer();
        navi(firebase.database().ref(ref)).once('value').then(function (snapshot) {
            var value = snapshot.val();
            def.resolve(value);
        });
        return def.promise;
    };
}]);
