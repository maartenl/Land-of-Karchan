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
                    $scope.errorDetails = null;
                    var restBase = Restangular.all('methods');
                    $scope.reload = function(alphabet) {
                        restBase.all(alphabet).getList()
                                .then(function(methods) {
                                    // returns a list of methods
                                    $scope.methods = methods;
                                    $scope.isNew = false;
                                });
                    };
                    $scope.remove = function(index) {
                        Restangular.one('methods', $scope.methods[index].name).remove().then(function() {
                            $scope.errorDetails = null;
                        }
                        , function(response) {
                            $scope.errorDetails = response.data;
                            alert(response.data.errormessage);
                        });
                    };
                    $scope.edit = function(index) {
                        $scope.method = $scope.methods[index];
                        $scope.isNew = false;
                        $scope.errorDetails = null;
                    };
                    $scope.disown = function(index) {
                        $scope.errorDetails = null;
                        $scope.methods[index].customDELETE("owner").then(function() {
                            $scope.errorDetails = null;
                        }
                        , function(response) {
                            $scope.errorDetails = response.data;
                            alert(response.data.errormessage);
                        });
                        $scope.methods[index].owner = null;
                    };
                    $scope.copy = function(index) {
                        $scope.method = Restangular.copy($scope.methods[index]);
                        $scope.isNew = true;
                        $scope.errorDetails = null;
                    };
                    $scope.create = function() {
                        $scope.isNew = true;
                        $scope.errorDetails = null;
                        $scope.method = {
                            name: "",
                            contents: "",
                            type: ""
                        };
                    };
                    $scope.update = function() {
                        if ($scope.isNew)
                        {
                            restBase.post($scope.method).then(function() {
                                $scope.errorDetails = null;
                            }
                            , function(response) {
                                $scope.errorDetails = response.data;
                                alert(response.data.errormessage);
                            });
                        }
                        else
                        {
                            $scope.method.put().then(function() {
                                $scope.errorDetails = null;
                            }
                            , function(response) {
                                $scope.errorDetails = response.data;
                                alert(response.data.errormessage);
                            });
                        }
                        $scope.isNew = false;
                    };
                });

