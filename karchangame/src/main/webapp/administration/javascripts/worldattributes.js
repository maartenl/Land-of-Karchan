/* 
 * Copyright (C) 2014 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
angular.module('karchan', ['restangular'])
        .config(function(RestangularProvider) {
            RestangularProvider.setBaseUrl('/karchangame/resources/administration');
            RestangularProvider.setRestangularFields({
                id: "name"
            });
            RestangularProvider.configuration.getIdFromElem = function(elem) {
                return elem["name"];
            };
        })
        .controller('MyController',
                function($scope, Restangular) {
                    var restBase = Restangular.all('worldattributes');
                    $scope.reload = function() {
                        restBase.getList()
                                .then(function(worldattributes) {
                                    // returns a list of users
                                    $scope.worldattributes = worldattributes;
                                    var firstAccount = worldattributes[0];
                                    firstAccount.contents += "wah!";
                                    firstAccount.put();
                                    $scope.isNew = false;
                                });
                    };
                    $scope.remove = function(index) {
                        Restangular.one('worldattributes', $scope.worldattributes[index].name).remove();
                    };
                    $scope.edit = function(index) {
                        $scope.worldattribute = $scope.worldattributes[index];
                        $scope.isNew = false;
                    };
                    $scope.copy = function(index) {
                        $scope.worldattribute = Restangular.copy($scope.worldattributes[index]);
                        $scope.isNew = true;
                    };
                    $scope.create = function() {
                        $scope.isNew = true;
                        $scope.worldattribute = {
                            name: "",
                            contents: "",
                            type: ""
                        };
                    };
                    $scope.update = function() {
                        if ($scope.isNew)
                        {
                            restBase.post($scope.worldattribute);
                        }
                        else
                        {
                            if (window.console) {
                                console.log($scope.worldattribute);
                            }
                            $scope.worldattribute.put();
                            // Restangular.one('worldattributes', $scope.worldattribute.name).put($scope.worldattribute);
                        }
                        $scope.isNew = false;
                    };
                    $scope.reload();
                });

