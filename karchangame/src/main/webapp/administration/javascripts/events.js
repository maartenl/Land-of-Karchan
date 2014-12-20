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
                id: "eventid"
            });
            RestangularProvider.configuration.getIdFromElem = function(elem) {
                return elem["eventid"];
            };
        })
        .controller('MyController',
                function($scope, Restangular) {
                    $scope.navigator = window.navigator;
                    $scope.errorDetails = null;
                    var restBase = Restangular.all('events');
                    $scope.setFilter = function() {
                        if ($scope.filter === null)
                        {
                            $scope.displayEvents = $scope.events;
                        } else
                        {
                            $scope.displayEvents = $scope.events.filter(function(element) {
                                return element.owner != null && element.owner.name === $scope.filter;
                            }
                            )
                        }
                    }
                    $scope.removeFilter = function() {
                        $scope.filter = null;
                        $scope.setFilter();
                    }
                    $scope.reload = function() {
                        restBase.getList()
                                .then(function(events) {
                                    // returns a list of events
                                    $scope.events = events;
                                    $scope.setFilter();
                                    $scope.isNew = false;
                                });
                    };
                    $scope.remove = function(index) {
                        Restangular.one('events', $scope.displayEvents[index].eventid).remove().then(function() {
                            $scope.errorDetails = null;
                        }
                        , function(response) {
                            $scope.errorDetails = response.data;
                            alert(response.data.errormessage);
                        });
                    };
                    $scope.edit = function(index) {
                        $scope.event = $scope.displayEvents[index];
                        $scope.isNew = false;
                        $scope.errorDetails = null;
                    };
                    $scope.disown = function(index) {
                        $scope.errorDetails = null;
                        $scope.displayEvents[index].customDELETE("owner").then(function() {
                            $scope.errorDetails = null;
                        }
                        , function(response) {
                            $scope.errorDetails = response.data;
                            alert(response.data.errormessage);
                        });
                        $scope.displayEvents[index].owner = null;
                    };
                    $scope.copy = function(index) {
                        $scope.event = Restangular.copy($scope.displayEvents[index]);
                        $scope.isNew = true;
                        $scope.errorDetails = null;
                    };
                    $scope.create = function() {
                        $scope.isNew = true;
                        $scope.errorDetails = null;
                        $scope.event = {
                            event: "",
                            description: "",
                            shortdescription: ""
                        };
                    };
                    $scope.update = function() {
                        if ($scope.isNew)
                        {
                            restBase.post($scope.event).then(function() {
                                $scope.errorDetails = null;
                            }
                            , function(response) {
                                $scope.errorDetails = response.data;
                                alert(response.data.errormessage);
                            });
                        }
                        else
                        {
                            $scope.event.put().then(function() {
                                $scope.errorDetails = null;
                            }
                            , function(response) {
                                $scope.errorDetails = response.data;
                                alert(response.data.errormessage);
                            });
                        }
                        $scope.isNew = false;
                    };
                    $scope.removeFilter();
                    $scope.reload();
                });

