'use strict';

var minesweeperApp = angular.module('minesweeperApp', ['ngAnimate', 'ui.bootstrap']);
minesweeperApp.controller('MinesweeperCtrl', function ($scope, $http, $uibModal, $interval) {

    $scope.game = null;
    $scope.board = null;
    $scope.level = 'EASY';
    $scope.minesLeftToMark = 0;
    $scope.timeTaken = 0;
    var timer = null;

    $scope.open = function (box, rowId, colId) {
        $http.get('/service/game/' + $scope.game.id + '/box?x=' + rowId + '&y=' + colId)
            .then(response => {
                updateBoxesAffected(response.data.boxesAffected);
                updateGameStatus(response.data.status);
            });
    };

    $scope.markMine = function (box, rowId, colId) {
        $http.get('/service/game/' + $scope.game.id + '/box/mine?x=' + rowId + '&y=' + colId)
            .then(response => {
                updateBoxesAffected(response.data.boxesAffected);
                updateGameStatus(response.data.status);
                if (box.status == 'CLOSED') {
                    if ($scope.minesLeftToMark > 0) {
                        $scope.minesLeftToMark--;
                    }
                } else {
                    $scope.minesLeftToMark++;
                }
            });
    };

    newGame($scope.level);

    $scope.newGame = function () {
        newGame($scope.level);
    };

    function newGame(level) {
        $http.get('/service/game/new?level=' + level)
            .then(response => {
                $scope.game = response.data;
                $scope.minesLeftToMark = $scope.game.complexity.mineCount;
                var board = {};
                $scope.board = $scope.game.board.boxes;
                $scope.timeTaken = 0;
                $interval.cancel(timer);
                timer = $interval(() => {
                    $scope.timeTaken = $scope.timeTaken + 1;
                }, 1000);
            });
    }

    function updateBoxesAffected(boxesAffected) {
        if (boxesAffected) {
            boxesAffected.forEach(boxPos => {
                $scope.board[boxPos.position.x][boxPos.position.y] = boxPos.box;
            });
        }
    }

    function updateGameStatus(status) {
        if (status) {
            $scope.game.status = status;
            if (status != 'IN_PLAY') {
                $interval.cancel(timer);
            }
        }
    }


});

/**
 * To handle right click. Copied from somewhere in the internet.
 */
minesweeperApp.directive('ngRightClick', function ($parse) {
    return function (scope, element, attrs) {
        var fn = $parse(attrs.ngRightClick);
        element.bind('contextmenu', function (event) {
            scope.$apply(function () {
                event.preventDefault();
                fn(scope, {$event: event});
            });
        });
    };
});

/**
 * To display time taken in seconds on a fixed 3 digit length.
 */
minesweeperApp.filter('numberFixedLen', function () {
    return function (n, len) {
        var num = parseInt(n, 10);
        len = parseInt(len, 10);
        if (isNaN(num) || isNaN(len)) {
            return n;
        }
        num = '' + num;
        while (num.length < len) {
            num = '0' + num;
        }
        return num;
    };
});