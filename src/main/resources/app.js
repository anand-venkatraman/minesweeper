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
        $http.get('/service/game/' + $scope.game.id + '/box?row=' + rowId + '&col=' + colId)
            .then(response => {
                updateBoxesAffected(response.data.boxesAffected);
                updateGameStatus(response.data.status);
            });
    };

    $scope.markMine = function (box, rowId, colId) {
        $http.get('/service/game/' + $scope.game.id + '/box/mine?row=' + rowId + '&col=' + colId)
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

    newGame();

    $scope.newGame = function () {
        newGame();
    };
    
    $scope.changeLevel = function() {
        $uibModal.open({
            animation: true,
            templateUrl: 'level.html',
            controller: 'ChangeLevelCtrl',
            size: 'sm'
        }).result.then((level) => {
            if (level){
                $scope.level = level.toUpperCase();
            }
            newGame();
        });
    };

    function newGame() {
        $http.get('/service/game/new?level=' + $scope.level)
            .then(response => {
                $scope.game = response.data;
                $scope.minesLeftToMark = $scope.game.mineCount;
                var boxes = [];
                for(var i=0; i < $scope.game.board.rows; i ++) {
                    var arr = [];
                    for(var j=0; j < $scope.game.board.columns; j ++) {
                        arr.push({
                            status: 'CLOSED',
                            adjacentMineCount: 0
                        });
                    }
                    boxes.push(arr);
                }
                $scope.board = boxes;
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
                $scope.board[boxPos.position.row][boxPos.position.col] = boxPos.box;
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
        var min = Math.floor(n / 60) 
        var sec = n %60;
        if (sec < 10) {
            sec = '0' + sec;
        }
        return min + ':' + sec;
    };
});

minesweeperApp.controller('ChangeLevelCtrl', function ($scope, $modalInstance) {
    $scope.level = '';
    
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
    
    $scope.ok = function() {
        $modalInstance.close($scope.level);
    }
});