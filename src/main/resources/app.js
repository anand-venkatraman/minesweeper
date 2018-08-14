'use strict';

var minesweeperApp = angular.module('minesweeperApp', ['ngAnimate', 'ui.bootstrap']);
minesweeperApp.controller('MinesweeperCtrl', function($scope,$http, $uibModal,$interval) {
    
    $scope.game = null;
    $scope.board = null;
    $scope.level = 'EASY';
    $scope.minesLeftToMark = 0;
    $scope.timeTaken = 0;
    var timer = null;
    
    $scope.open = function(box) {
        $http.get('/service/game/' + $scope.game.id + '/box?x=' + box.position.x + '&y=' + box.position.y)
            .then(response => {
                if (response.data.boxesAffected) {
                    response.data.boxesAffected.forEach(box => {
                        $scope.board[box.position.x][box.position.y] = box;
                    });
                }
                if (response.data && response.data.status) {
                    $scope.game.status = response.data.status;
                    if (response.data.status != 'IN_PLAY') {
                        $interval.cancel(timer);
                    }
                }
        });
    };
    
    $scope.markMine = function(box) {
        $http.get('/service/game/' + $scope.game.id + '/box/mine?x=' + box.position.x + '&y=' + box.position.y)
            .then(response => {
                if (response.data.boxesAffected) {
                    response.data.boxesAffected.forEach(box => {
                       $scope.board[box.position.x][box.position.y] = box;  
                    });
                }
                if (box.status == 'CLOSED') {
                    $scope.minesLeftToMark --;
                } else {
                    $scope.minesLeftToMark ++;
                }
                $scope.game.status = response.data.status;
                if (response.data && response.data.status) {
                    $scope.game.status = response.data.status;
                    if (response.data.status != 'IN_PLAY') {
                        $interval.cancel(timer);
                    }
                }
        });
    };
    
    newGame($scope.level);
    
    $scope.newGame = function() {
        newGame($scope.level);
    };
    
    function newGame(level) {
        $http.get('/service/game/new?level=' + level)
            .then(response => {
            $scope.game = response.data;
            $scope.minesLeftToMark = $scope.game.complexity.mineCount;
            var board = {};
            response.data.board.boxes.forEach(box => {
               board[box.position.x + '~~' + box.position.y] = box; 
            });
            $scope.board = [];
            for(var i=0; i < $scope.game.complexity.rows; i++) {
                var arr = [];
                for(var j=0; j < $scope.game.complexity.columns; j++) {
                    arr.push(board[i + '~~' + j]);
                }
                $scope.board.push(arr);
            }
            $scope.timeTaken = 0;
            $interval.cancel(timer);
            timer = $interval(() => {
                    $scope.timeTaken = $scope.timeTaken + 1;
            }, 1000);
        });
    }
    
    
});

/**
 * To handle right click. Copied from somewhere in the internet.
 */
minesweeperApp.directive('ngRightClick', function($parse) {
    return function(scope, element, attrs) {
        var fn = $parse(attrs.ngRightClick);
        element.bind('contextmenu', function(event) {
            scope.$apply(function() {
                event.preventDefault();
                fn(scope, {$event:event});
            });
        });
    };
});

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