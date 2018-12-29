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
        <link rel="stylesheet" href="/css/bootstrap.min.css"/>
        <link rel="stylesheet" href="/css/karchan.css"/>
        <title>Land of Karchan</title>
    </head>

    <body>
        <div class="container-fluid">
            <div class="media">
                <img class="mr-3" src="/images/gif/dragon.gif" alt="Dragon">
                <div class="media-body">
                    <h1 class="mt-0">Land of Karchan ${activeMenu} ${template} ${url}</h1>
                </div>
            </div>
        </div>
        <div class="container-fluid">
            <nav class="navbar navbar-expand-lg">
                <ul class="nav">

<#list menus as menu>
<#if menu.subMenu?size == 0>
                    <li class="nav-item">
                        <a class="nav-link<#if activeMenu == menu.name> active</#if>" href="${menu.url}">${menu.name}</a>
                    </li>
<#else>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle<#if activeMenu == menu.name> active</#if>" href="${menu.url}" id="${menu.name}Dropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            ${menu.name}
                        </a>
                        <div class="dropdown-menu" aria-labelledby="${menu.name}Dropdown">
<#list menu.subMenu as subber>
<#if subber.name == "--">
                            <div class="dropdown-divider"></div>
<#else>
                            <a class="dropdown-item<#if activeMenu == menu.name> active</#if>" href="${subber.url}">${subber.name}</a>
</#if>
</#list>
                        </div>
                    </li>
</#if>
</#list>
                </ul>
            </nav>
        </div>
        <div class="container-fluid">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
<#list breadcrumbs as breadcrumb>  
                    <li class="breadcrumb-item"><a href="${breadcrumb.url}">${breadcrumb.name}</a></li>
</#list>
<#if lastBreadcrumb??>
                    <li class="breadcrumb-item active" aria-current="page">${lastBreadcrumb.name}</li>
</#if>
                </ol>
            </nav>
        </div>
');
replace into templates 
(id, name, created, modified, content)
values(2, "footer", now(), now(), 
'	<!-- Optional JavaScript -->
        <!-- jQuery first, then Popper.js, then Bootstrap JS -->
        <script src="/js/jquery-3.3.1.min.js"></script>
        <script src="/js/popper.min.js"></script>
        <script src="/js/bootstrap.min.js"></script>
    </body>
</html>');
replace into templates 
(id, name, created, modified, content)
values(3, "index", now(), now(), '
        <div class="container-fluid">
            <div class="row">
                <div class="col">
                    <div class="card m-3">
                        <div class="card-header">
                            Play the Game!
                        </div>
                        <div class="card-body">
                            <p class="card-text">
                            <form id="logonForm" method="post" onsubmit="logon(); return false;">
                                <div class="form-group">
                                    <label for="name">Name <span class="form-required">*</span></label>
                                    <input type="text" maxlength="36" name="name" class="form-control" id="name" aria-describedby="characterHelp" placeholder="Enter character name" required>
                                    <small id="characterHelp" class="form-text text-muted">If you wish to create a new character, click <a href="#">here</a>.</small>
                                </div>
                                <div class="form-group">
                                    <label for="password">Password <span class="form-required">*</span></label>
                                    <input type="password" class="form-control" id="password" maxlength="39" name="password" size="16" placeholder="Password" required>
                                </div>
                                <button type="submit" class="btn btn-primary">Submit</button>
                            </form>
                            </p>
                        </div>
                    </div>
                    <div class="card m-3">
                        <div class="card-header">
                            Contact Us
                        </div>
                        <div class="card-body">
                            <p class="card-text"> If you need to contact us, you can do so at deputiesofkarchan at outlook.com. </p>
                        </div>
                    </div>            
                    <div class="card m-3">
                        <div class="card-header">
                            Who
                        </div>
                        <div class="card-body">
                            <p class="card-text" id="karchan_who">Loading content. Please hold...</p>
                        </div>
                    </div>            
                    <div class="card m-3">
                        <div class="card-header">
                            Badges
                        </div>
                        <div class="card-body">
                            <p class="card-text">Using <a href="https://letsencrypt.org/" target="_blank">Let''s Encrypt</a> for SSL.</p>
                        </div>
                    </div>            
                </div>
                <div class="col-12 col-lg-8">
                    <div class="card m-3">
                        <div class="card-header">
                            Blogs
                        </div>
                        <div class="card-body">
                            <p class="card-text">

<#list blogs as blog> 
                            <div class="card m-1">
                                <div class="card-body">
                                    <h5 class="card-title">  
                                        <a href="#">${blog.title}</a>
                                    </h5>
                                    <h6 class="card-subtitle mb-2 text-muted">Published Date ${blog.createDate}</h6>
                                    <p class="card-text">
                                        ${blog.content}
                                    </p>
                                    <small class="text-muted">By ${blog.name}.</small>
                                </div>
                            </div>
</#list>
                            <div class="card m-1">
                                <div class="card-body">
                                    <h5 class="card-title">  
                                        <a href="#">Fair warning!</a>
                                    </h5>
                                    <h6 class="card-subtitle mb-2 text-muted">Published Date 12/15/18 9:39 PM</h6>
                                    <p class="card-text">
                                        <img alt="T" src="/images/gif/letters/t.gif" style="float: left;">
                                        he upgrade of the database has taken place.</p>
                                    <p class="card-text">
                                        It means that all changes committed to the Wiki and the CMS from now on will be lost when the upgrade takes place.</p>
                                    <p class="card-text">
                                        If you don''t like this, kindly delay any changes you wish to make until after the upgrade.</p>
                                    <p class="card-text">
                                        Thank you.</p>
                                    <p class="card-text">
                                        High regards,</p>
                                    <small class="text-muted">By Karn Ruler of Karchan, Keeper of the Key to the Room of Lost Souls.</small>
                                </div>
                            </div>
                        </div>
                    </div>            
                </div>
            </div>
        </div>
        <script src="/javascripts/who.js" type="text/javascript"></script>
        <script type="text/javascript">
            $(document).ready(function () {
                showWho("short");
            });
        </script>
');
replace into templates 
(id, name, created, modified, content)
values(4, "notFound", now(), now(), '<div class="container">
  <div class="alert alert-danger" role="alert">
    <p>Template "<span class="alert-link">${template}</span>" not found! Please alert Karn and the deputies regarding this issue.</p>
    <hr>
    <p>Click <a href="/index.html" class="alert-link">here</a> to return to the Homepage.</p>
  </div>
</div>');
replace into templates 
(id, name, created, modified, content)
values(5, "introduction", now(), now(), '
        <div class="container">
            <p>This is the MUD (multi-user dungeon) called Land of Karchan, and it is one of the first real MUD''s on the World Wide Web. If you are new here, the game is very simple. Just type below, a fictive name (and make it a little original) and a password (to prevent people from using your character with devastating problems). If you already have a character here, you will automatically get where you were when you left this great game. Look at the following links:</p> <ul> <li><a href="/logon">Loggging into Karchan</a></li> </ul> <ul> <li><a href="/the-guide">Manual and Explanation</a></li> </ul> <p>&nbsp;If you have entered your name and password, please hit the Submit button. (Remember that you are not supposed to type multiple names, just one name will suffice and more will give grave problems.) Or hit Clear for a new try...</p> <p>If you wish to create a new character, click <a href="/new-character">here</a>.</p> <p><em>Karn (Ruler of Karchan, Keeper of the Key to the Room of Lost Souls)</em> <a href="/deputy-logon">&gt;</a></p> <p>&nbsp;</p> 
        </div>
');
replace into templates 
(id, name, created, modified, content)
values(6, "links", now(), now(), '
        <div class="container">
            <p>Hello, and welcome to the <em>Links</em> page. &nbsp;Here I put links to other homepages concerning both Karchan (indirectly and directly) and anything relating to fantasy used in the game.</p>

            <p>&nbsp;</p>

            <p><strong><span style="font-size: large;">Karchanian Repositories</span></strong></p>

            <p>The Land of Karchan Wiki - <a href="http://lokwiki.wikispaces.com/" target="_blank" title="The
                                             Land of Karchan Wiki">http://lokwiki.wikispaces.com/</a></p>

            <p>Cheat Guide for Newbs -&nbsp;<span style="color: #3b3b3b; font-family: Georgia, ''Times New
                                                  Roman'', Times, serif; font-size: 14.399999618530273px; line-height:
                                                  16.799999237060547px;">http://karchanhelp.webs.com</span></p>

            <p>&nbsp;</p>

            <p><strong><span style="font-size: large;">Guilds of Karchan</span></strong></p>

            <p>The Knights of Karchan - <a href="http://www.theknightsofkarchan.webs.com/frames.html" target="_blank" title="The Land of Karchan Wiki">http://www.theknightsofkarchan.webs.com/frames.html</a></p>

            <p>The Magii of the Inner Flame - <a href="http://www.magiiflame.webs.com/" target="_blank" title="The Magii of the Inner Flame">http://www.magiiflame.webs.com/</a></p>

            <p>&nbsp;</p>

            <p><span style="font-size: large;"><strong>Related Fantasy Links</strong></span></p>

            <p><span style="font-size: x-small;">Links to wikis, etc., for the literature that has inspired the creation and evolution of the game.</span></p>

            <p>The Lord of the Rings Wikia - <a href="http://lotr.wikia.com/" target="_blank" title="The Lord of the Rings Wikia">http://lotr.wikia.com/</a></p>

            <p>T.H. White''s The Once and Future King -&nbsp;<a href="http://en.wikibooks.org/wiki/The_Once_and_Future_King">http://en.wikibooks.org/wiki/The_Once_and_Future_King</a></p>

            <p>Douglas Adams'' The Hitchhiker''s Guide to the Universe -&nbsp;<a href="http://hitchhikers.wikia.com/wiki/The_Hitchhiker''s_Guide_to_the_Galaxy">http://hitchhikers.wikia.com/wiki/The_Hitchhiker''s_Guide_to_the_Galaxy</a></p>

            <p>&nbsp;</p>

            <p><strong><span style="font-size: large;">Programming Karchan Homepages</span></strong></p>

            <ul>
                <li>The Homepage of MMud - <a href="http://maartenl.github.com/Land-of-Karchan/">http://maartenl.github.com/Land-of-Karchan/</a></li>
                <li>The Source Code of MMud - <a href="https://github.com/maartenl/Land-of-Karchan">https://github.com/maartenl/Land-of-Karchan</a></li>
            </ul>

            <p>&nbsp;</p>

            <h1>Glossary</h1>

            <dl>
                <dt>MMud</dt>
                <dd>the source code or gaming engine on which Land of Karchan is built.</dd>
                <dt>Sourceforge</dt>
                <dd>A free Software Project Development Webpage</dd>
            </dl>
        </div>
');
replace into templates 
(id, name, created, modified, content)
values(7, "logon", now(), now(), '
        <div class="container-fluid">
            <div class="row">
                <div class="col">
                    <div class="card">
                        <div class="card-header">
                            Play the Game!
                        </div>
                        <div class="card-body">
                            <p class="card-text">
                            <form id="logonForm" method="post" onsubmit="logon(); return false;">
                                <div class="form-group">
                                    <label for="name">Name <span class="form-required">*</span></label>
                                    <input type="text" maxlength="36" name="name" class="form-control" id="name" aria-describedby="characterHelp" placeholder="Enter character name" required>
                                    <small id="characterHelp" class="form-text text-muted">If you wish to create a new character, click <a href="#">here</a>.</small>
                                </div>
                                <div class="form-group">
                                    <label for="password">Password <span class="form-required">*</span></label>
                                    <input type="password" class="form-control" id="password" maxlength="39" name="password" size="16" placeholder="Password" required>
                                </div>
                                <button type="submit" class="btn btn-primary">Submit</button>
                            </form>
                            </p>
                        </div>
                    </div>
                </div>                
            </div>
        </div>
');
replace into templates 
(id, name, created, modified, content)
values(8, "the_law", now(), now(), '
        <div class="container">
            <ol> <li><strong><span style="font-size: large;">Thou art responsible for the actions of thyne character at all times!</span></strong><br> The actions of the characters are, and always will be, the responsibility of the persons that created them. Signing into a character that is not yours without permission is considered hacking and will be punished as such.</li> <li><strong><span style="font-size: large;">Thou shalt treat what deputies tell thee as being law!</span></strong><br> Their decisions are final, there is no parole or appeal possible.&nbsp; If you dislike how they rule, you have the right to stop playing this mud. It is unlawful to lie to a deputy or otherwise hinder any deputy investigation.</li> <li><strong><span style="font-size: large;">Thou Shalt Treat Others With Respect!</span></strong><br> Harassment will not be tolerated.&nbsp; For those of you who don''t know what harassing is, here''s a definition:<br> <em>harassment = if people think that you are harassing them, then you are harassing them!</em><br> You will follow the rules of Netiquette.&nbsp; Abusive words, slang, spamming, acrimony or any other kind of language with the purpose to harass or be hurtful also fall under this header.</li> <li><strong><span style="font-size: large;">Thou shalt not try to break/hack into the MUD</span></strong><br> Attempting to break/hack into the MUD and failing also falls under this header.&nbsp; Attempting to break/hack into the MUD "just for fun" or "because I was bored" or "to see if I could" also fall under this header.&nbsp; Just saying that you are going to hack into the mud, or have hacked into the mud when you really haven''t also fall under this heading.&nbsp; Breaking this rule is grounds for <strong>IMMEDIATE BANISHMENT</strong> from the game!</li> <li><strong><span style="font-size: large;">Thou shalt not cheat</span></strong><br> Cheating will not be tolerated.&nbsp; If you find a bug, report it, then do not use it.&nbsp; No multiple logins, multi-playing, or otherwise helping your own characters with other characters you own.</li> <li><strong><span style="font-size: large;">Thou shalt not give out quest solutions.</span></strong><br> If a fellow player is stuck on a quest it is allowed to give them small hints.&nbsp; It is also allowed for multiple players who do not know the solution to combine their efforts in a particular quest.</li> <li><strong><span style="font-size: large;">Thou shalt type Quit, every time thou leavest this great game.</span></strong><br> If you don''t and try to log in at a later time, the computer may indicate that another session is still active.&nbsp; It is rude to leave people talking to a husk.&nbsp; This is more a <em>Guideline</em>&nbsp; than a solid black-and-white rule, but still punishable if excessively or abusively committed.</li> <li><span style="font-size: large;"><strong>Thou Shalt Respect the Works of Others</strong></span><br> This is a story being written by many.&nbsp; Many have come before you.&nbsp; Many will come after.&nbsp; Do not copy, hinder, ridicule, or otherwise harass the stories, roleplaying, or ideas of others. Keep your criticizing to yourself.&nbsp; When asked for it, be constructive.&nbsp; Having a similar name or appearance happens; having the same back-story does not. Do not use the creative material of another player without their permission.</li> <li><span style="font-size: large;"><strong>Thou Shalt NOT </strong></span><strong><span style="font-size: large;">Divulge Confidences!</span></strong><br> Do not share the real name, personal information, Out-of-Character (ooc) opinion or Alternate Characters (Alts) of any other player of this game.&nbsp; If they wish this information to be shared, they will do so themselves.&nbsp; If you wish this information to be shared, do so at your own risk.</li> <li><span style="font-size: large;"><strong>Thou Shalt NOT </strong></span><strong><span style="font-size: large;">fully ignore deputies!</span></strong><br> Deputies need to be able to interact freely with the players, especially if an issue comes up that needs to be mediated or addressed.</li> <li><strong><span style="font-size: large;">Thou shalt NOT log on as more than two characters at a time.</span></strong><br> In order to log on as two different characters, you must use two different browsers.</li> <li><strong><span style="font-size: large;">Thou shalt NOT threaten!</span></strong><br> Threats of real life harm against yourself or others, either in game or out of game, are grounds for punishment. This punishment could result in immediate and permanent banishment from the game. As always, such will be handled on a case by case basis at the discretion of the deputies.</li> <li><strong><span style="font-size: large;">Ignorance of these here written and unwritten rules is no excuse.</span></strong><br> Changes to these rules are at the discretion of the creator.&nbsp; Make sure to keep updated.</li> </ol>         
        </div>
');
replace into templates 
(id, name, created, modified, content)
values(9, "who", now(), now(), '
        <div class="container-fluid">
            <div class="row">
                <div class="col">
                    <div class="card">
                        <div class="card-header">
                            Who
                        </div>
                        <div class="card-body">
                            <p class="card-text" id="karchan_who">Loading content. Please hold...</p>
                        </div>
                    </div>            
                </div>
            </div>
        </div>
        <script src="/javascripts/who.js" type="text/javascript"></script>
        <script type="text/javascript">
            $(document).ready(function () {
            showWho("long");
            });
        </script>
');
