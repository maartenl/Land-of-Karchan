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
                    $scope.navigator = window.navigator;
                    var restBase = Restangular.all('worldattributes');
                    $scope.reload = function() {
                        restBase.getList()
                                .then(function(worldattributes) {
                                    // returns a list of worldattributes
                                    $scope.worldattributes = worldattributes;
                                    $scope.isNew = false;
                                });
                    };
                    $scope.remove = function(index) {
                        Restangular.one('worldattributes', $scope.worldattributes[index].name).remove().then(function() {
                            $scope.errorDetails = null;
                        }
                        , function(response) {
                            $scope.errorDetails = response.data;
                            alert(response.data.errormessage);
                        });
                    };
                    $scope.edit = function(index) {
                        $scope.worldattribute = $scope.worldattributes[index];
                        $scope.isNew = false;
                        $scope.errorDetails = null;
                    };
                    $scope.disown = function(index) {
                        $scope.errorDetails = null;
                        $scope.worldattributes[index].customDELETE("owner").then(function() {
                            $scope.errorDetails = null;
                        }
                        , function(response) {
                            $scope.errorDetails = response.data;
                            alert(response.data.errormessage);
                        });
                        $scope.worldattributes[index].owner = null;
                    };
                    $scope.copy = function(index) {
                        $scope.worldattribute = Restangular.copy($scope.worldattributes[index]);
                        $scope.isNew = true;
                        $scope.errorDetails = null;
                    };
                    $scope.create = function() {
                        $scope.isNew = true;
                        $scope.errorDetails = null;
                        $scope.worldattribute = {
                            name: "",
                            contents: "",
                            type: ""
                        };
                    };
                    $scope.update = function() {
                        $scope.errorDetails = null;
                        if ($scope.isNew)
                        {
                            restBase.post($scope.worldattribute).then(function() {
                                $scope.errorDetails = null;
                            }
                            , function(response) {
                                $scope.errorDetails = response.data;
                                alert(response.data.errormessage);
                            });
                        }
                        else
                        {
                            $scope.worldattribute.put().then(function() {
                                $scope.errorDetails = null;
                            }
                            , function(response) {
                                $scope.errorDetails = response.data;
                                alert(response.data.errormessage);
                            });
                        }
                        $scope.isNew = false;
                    };
                    $scope.reload();
                });

