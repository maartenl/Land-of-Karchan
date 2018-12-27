drop table if exists templates;

create table templates (
  id bigint(20) NOT NULL primary key,
  name varchar(90) NOT NULL DEFAULT '',
  created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  content text not null,
  INDEX nameindex (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

replace into templates 
(id, name, created, modified, content)
values(1, "header", now(), now(), 
'<!doctype html>
<html lang="en">
    <head>
        <!-- Required meta tags -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="css/bootstrap.min.css"/>
        <link rel="stylesheet" href="css/karchan.css"/>
        <title>Land of Karchan</title>
    </head>

    <body>
        <div class="container-fluid">
            <div class="media">
                <img class="mr-3" src="/images/gif/dragon.gif" alt="Dragon">
                <div class="media-body">
                    <h1 class="mt-0">Land of Karchan</h1>
                </div>
            </div>
        </div>
        <div class="container-fluid">
            <nav class="navbar navbar-expand-lg">
                <ul class="nav">
                    <li class="nav-item">
                        <a class="nav-link" href="#">Welcome</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="logon.html">Logon</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="introduction.html">Introduction</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="newcharacter.html">New character</a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="chronicles/chronicles.html" id="chroniclesDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            Chronicles
                        </a>
                        <div class="dropdown-menu" aria-labelledby="chroniclesDropdown">
                            <a class="dropdown-item" href="chronicles/map.html">Map</a>
                            <a class="dropdown-item" href="chronicles/history.html">History</a>
                            <div class="dropdown-divider"></div>
                            <a class="dropdown-item" href="chronicles/people.html">People</a>
                            <a class="dropdown-item" href="chronicles/fortunes.html">Fortunes</a>
                            <a class="dropdown-item" href="chronicles/guilds.html">Guilds</a>
                        </div>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="who.html">Who</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="the-law.html">The Law</a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="help/help.html" id="helpDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            Help
                        </a>
                        <div class="dropdown-menu" aria-labelledby="helpDropdown">
                            <a class="dropdown-item" href="help/status.html">Status</a>
                            <a class="dropdown-item" href="help/guide.html">The Guide</a>
                            <a class="dropdown-item" href="help/tech_specs.html">Tech Specs</a>
                            <a class="dropdown-item" href="help/source.html">Source</a>
                            <a class="dropdown-item" href="help/security.html">Security</a>
                        </div>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="links.html">Links</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="wiki.html">Wiki</a>
                    </li>
                </ul>
            </nav>
        </div>
        <div class="container-fluid">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item active" aria-current="page">Welcome</li>
                </ol>
            </nav>
        </div>
');
replace into templates 
(id, name, created, modified, content)
values(2, "footer", now(), now(), 
'	<!-- Optional JavaScript -->
        <!-- jQuery first, then Popper.js, then Bootstrap JS -->
        <script src="js/jquery-3.3.1.min.js"></script>
        <script src="js/popper.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
    </body>
</html>');
replace into templates 
(id, name, created, modified, content)
values(3, "main", now(), now(), "mrbear ${user}   <#list blogs as blog> ${blog.content} </#list>");

