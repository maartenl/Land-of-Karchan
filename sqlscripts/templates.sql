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

        <link rel="icon" href="/favicon.ico">

        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="/css/bootstrap.min.css"/>
        <link rel="stylesheet" href="/css/karchan.css"/>
        <title>Land of Karchan</title>
	<!-- Optional JavaScript -->
        <!-- jQuery first, then Popper.js, then Bootstrap JS -->
        <script src="/js/jquery-3.3.1.min.js"></script>
        <script src="/js/popper.min.js"></script>
        <script src="/js/bootstrap.min.js"></script>
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
                            <a class="dropdown-item<#if activeMenu == subber.name> active</#if>" href="${subber.url}">${subber.name}</a>
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
'    </body>
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
                                    <small id="characterHelp" class="form-text text-muted">If you wish to create a new character, click <a href="/new_character.html">here</a>.</small>
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
                            <p class="card-text">Using <a href="https://letsencrypt.org/" class="badge badge-info" target="_blank">Let''s Encrypt</a> for SSL.</p>
                        </div>
                    </div>            
                </div>
                <div class="col-12 col-lg-8">
                    <div class="card m-3">
                        <div class="card-header">
                            <a href="/blogs/index.html">Blogs</a>
                        </div>
                        <div class="card-body">
                            <p class="card-text">

<#list blogs as blog> 
                            <div class="card m-1">
                                <div class="card-body">
                                    <h5 class="card-title">  
                                        <a href="/blogs/${blog.urlTitle}.html">${blog.title}</a>
                                    </h5>
                                    <h6 class="card-subtitle mb-2 text-muted">Published Date ${blog.createDate?datetime}</h6>
                                    <p class="card-text">
                                        ${blog.content}
                                    </p>
                                    <small class="text-muted">By ${blog.name}.</small>
                                </div>
                            </div>
</#list>                           
                        </div>
                    </div>            
                </div>
            </div>
        </div>
        <script src="/javascripts/who.js" type="text/javascript"></script>
        <script src="/javascripts/logon.js" type="text/javascript"></script> 
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
    Template "<span class="alert-link">${template}</span>" not found! Please alert Karn and the deputies regarding this issue.
  </div>
  <p>Click <a href="/index.html">here</a> to return to the Homepage.</p>
</div>');
replace into templates 
(id, name, created, modified, content)
values(5, "introduction", now(), now(), '
        <div class="container">
            <p>This is the MUD (multi-user dungeon) called Land of Karchan, and it is one of the first real MUD''s on the World Wide Web. If you are new here, the game is very simple. Just type below, a fictive name (and make it a little original) and a password (to prevent people from using your character with devastating problems). If you already have a character here, you will automatically get where you were when you left this great game. Look at the following links:</p> <ul> <li><a href="/logon.html">Loggging into Karchan</a></li> </ul> <ul> <li><a href="/help/guide.html">Manual and Explanation</a></li> </ul> <p>&nbsp;If you have entered your name and password, please hit the Submit button. (Remember that you are not supposed to type multiple names, just one name will suffice and more will give grave problems.) Or hit Clear for a new try...</p> <p>If you wish to create a new character, click <a href="/new_character.html">here</a>.</p> <p><em>Karn (Ruler of Karchan, Keeper of the Key to the Room of Lost Souls)</em> <a href="/deputy-logon">&gt;</a></p> <p>&nbsp;</p> 
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
                                    <small id="characterHelp" class="form-text text-muted">If you wish to create a new character, click <a href="/new_character.html">here</a>.</small>
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
        <script src="/javascripts/logon.js" type="text/javascript"></script> 
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

-- Chronicles --

replace into templates 
(id, name, created, modified, content)
values(10, "chronicles/index", now(), now(), '<div class="container">
<p><img alt="Land of
Karchan from Afar" height="300" src="/images/jpeg/Karchan1.jpg" style="vertical-align: middle; display: block; margin-left: auto;
margin-right: auto;" width="400" /></p>

<p>This page is dedicated to the History of the Land of Karchan. In here is most of the Legends and Old Stories located. Some of these are still told among the inhabitants of the land. Others have passed into the realm of forgetfullness a long long time ago. In it is also written an account of the beginning of this world.</p>

<p>Use these stories and tall tales to get a clear insight into the History of an entire world, and seek to improve upon it, or use it to your best advantage.</p>

<p>&nbsp;</p>

<div><em>Karn (Ruler of Karchan, Keeper of the Key to the Room of Lost Souls)</em></div>
</div>');

replace into templates 
(id, name, created, modified, content)
values(11, "chronicles/map", now(), now(), '<div class="container"><p>This page contains a map of the known world, as rendered by one of the game''s greatest artists and fondest players.</p>

<p><span style="font-size:
x-small;"><em>Algol&nbsp; </em><em>(Chairman of the Chamber of Oracles)</em></span></p>

<p><img alt="The Known World" src="/images/gif/map_final2.gif" style="display: block; margin-left: auto; vertical-align: middle; margin-right: auto; width: 663px; height: 502px;" /></p>
</div>
');
replace into templates 
(id, name, created, modified, content)
values(12, "chronicles/history", now(), now(), '<div class="container"><p style="text-align: right;"><a id="TABLE" name="TABLE"></a>Pendulis, 18 September 1153 according the Common Calendar</p>

<p><a href="#Intro">Chapter 1. Introduction</a></p>

<p><a href="#beginning">Chapter 2. The Tale of the Beginning</a></p>

<p><a href="#Rakah">Chapter 3. Rakah and Darkness</a></p>

<p><a href="#First">Chapter 3.1 First Sense</a></p>

<p><a href="#Second">Chapter 3.2 Second Sense</a></p>

<p><a href="#Third">Chapter 3.3 Third Sense</a></p>

<p><a href="#Fourth">Chapter 3.4 Fourth and Fifth Sense</a></p>

<p><a href="#Sixth">Chapter 3.5 The Sixth Sense</a></p>

<p><a href="#Genesis">Chapter 3.6 Genesis</a></p>

<p><a href="#KarnZahnos">Chapter 4. Karn and Zhanos</a></p>

<p><a href="#Sunrise">Chapter 4.1 Sunrise</a></p>

<p><a href="#Gods">Chapter 5 Creation of the Gods</a></p>

<p><a href="#InTheMeanTime">Chapter 6 In the mean time...</a></p>

<p><a href="#CreationOfRaces">Chapter 7 Creation of Races</a></p>

<p><a href="#ThreeBooks">Chapter 8. The Three Books of Evil</a></p>

<p><a href="#FirstBook">Chapter 8.1 The Book of Power</a></p>

<p><a href="#SecondBook">Chapter 8.2 The Book of Wrath</a></p>

<p><a href="#ThirdBook">Chapter 8.3 The Book of Destruction</a></p>

<p><a href="#Downfall">Chapter 9. Downfall of Zhanos</a></p>

<p><a href="#Shard">Chapter 10. The Tale of the Shard</a></p>

<p><a name="Intro"></a></p>

<h2><a href="#TABLE">Chapter 1. &nbsp;&nbsp;&nbsp;Introduction</a></h2>
Allow me to introduce myself first. I am Algol, Chronicler of the Past, Present and Future of the Land of Karchan. I have been chosen to chronicle the amazing history of the Land of Karchan, or of the entire planet Kúri of which the Land of Karchan occupies the major part. Now this history is quite amazing, filled with strange creatures, powerful magic, and amazing stories. This story is also the story of Good against Evil (and vice versa) like any good story should be. Both Good and Evil are intrinsically endorsed with two abilities. The first is to exist, and the second to multiply. Needless to say, both don''t get along very much and both try to reign over the other. But, as Good will try to co-exist, Evil tries to overcome, annihilate, terminate, and destroy totally. Other than that they mostly have the same goals. ''How did it all begin?'', you may ask yourself and a very good question it would appear, and instructive too for most races nowadays on Kúri have only a limited knowledge of the time before their first appearance in the world and only the Elves remember some of that which was.

<p>&nbsp;</p>

<p>Perhaps you are wondering how I can write about the entire History when that History is still incomplete, and how I know all about History? Sometimes I do too. The fact is that I am Chronicler and Chairman of the Guild of Oracles. Besides the Present, I can see the Past and the Future, even more so than most people. That was one of the reasons why I, of all people, was chosen to Chronicle this infinite History of the Land of Karchan. But enough about me, let me begin with my story.</p>

<p><a name="beginning"></a></p>

<h2><a href="#TABLE">Chapter 2. The Tale of the Beginning</a></h2>

<p>Ages ago before war, before peace, before land and sea, before time, there was only Existence, for naught had yet been created. Within Existence came Awareness, persisting without time, without thought, without meaning, though it felt no pain, for not even that had yet been created.</p>
<a href="#TABLE"> </a>

<p>There came a point when Awareness illuminated a path which it had trodden before. With "memory" came a sense of Time, and hence Time was born. Awareness grew to encircle the new concept, and in the union of the two came Thought, the force of evolution of Awareness through Time. But Thought was only full of emptiness, for naught existed of which to think. Time was a renegade, and it proceeded around throught the void by itself, relentlessly marching, only toying with Awareness when the mood struck.</p>
<a href="#TABLE"> </a>

<p>Finally, Time mingled with Thought enough to sire a child, and this child was called Dreaming. The aspect allowed Thought to run and play with concepts which did not or could not exist. Awareness and Dreaming led to Emotion, a secret child behind the back of thought who was too busy with Dreaming''s other delights to notice. Awareness regarded Thought as necessary through Thought''s own arguments, though it felt Emotion''s importance through the language of feeling, and it found itself unable to choose between the two. Dreaming came to Awareness''s call of distress, forming a concept of an Other to which Awareness could put such difficult questions. Other was formed in the image of Awareness through Thought''s powerful abilities, though Thought found itself unable to make Other more powerful than Awareness was already.</p>
<a href="#TABLE"> </a>

<p>Awareness and Other asked each other questions, but neither could answer any better than Awareness could originally. Still, each of them found in the other a sensation of companionship and comfort despite the confusion and difficulty that Awareness constantly presented. Together, they grew and learned, combining their Thoughts, sharing their Emotions, experiencing each other''s Awareness, letting each other dance through their Dreams until they became as one, a whole composed of two independent parts, each amplified by the other. And their dreams were filled with wonder, both with the answers they had found as well as their questions, and in these dreams was a new child born known as Reality, in which they placed all of their knowledge.</p>
<a href="#TABLE"> </a>

<p>From Reality the two removed the ability to create new aspects, for by now the two had enough children to worry about, and Time had become quite a bully. Reality wept at being so restricted, but the two explained that Reality had within it all that Existence had yet created. It was explained that Reality had the task of sorting out all the dilemmas which had manifested, and that once the answers were found, power would again be restored to Reality to change itself as it would see fit. Reality retained its bitter sense of loss, but accepted the challenge.</p>
<a href="#TABLE"> </a>

<p>Time was not to enjoy such and easy solution, however. Time found the quarrels of Emotion and Thought amusing, and so Time used its relentless abilities upon Reality, forcing the mostly incapacitated child to bow to its will. The two watched this and grew displeased. They could not openly defy Time, for Time was nearly as powerful as they and had intertwined itself deeply into Existence. They could, however, grant Reality aid. Dreaming again aided the two in their time of need, suggesting that perhaps if there were more than one Reality, Time would not have enough strength to annoy all of them at once. Though they had no intention of spawning more children until their current problems were solved, they did find a compromise.</p>
<a href="#TABLE"> </a>

<p>Against Reality they brought their wills, splitting his consciousness into a billion trillion fragments, each possessing bits and pieces of the knowledge of Existence. All the tiny bits of Reality coalesced and formed the Multiverse with its pockets of realities all similar and yet different. And within them the truths coalesced into the forces of the world. Like the original two, all things contained the forces of thought and emotion struggling against each other. Balance and imbalance, light and dark, selfish and selfless, all ranges were created and distributed amongst the streaming new woner Reality had become, each tiny bit working with the others in strange and sometimes undiscernable ways to reach the ultimate answer that the two wait for silently..."</p>
<a href="#TABLE"> </a>

<p>"We have been bestowed with great forces of both thought and emotion. We can harness their powers for the good of all, or we can let them war with us as so many others have done. Time still holds reign over our and all other lands, and this is an aspect which we must simply succumb to, for resistance is dangerous and eventually futile.</p>
<a href="#TABLE"> </a>

<p>Also, like the tale, all of us have deep within a need for an "other", one to share our awareness, emotions and dreams. This quest will be repeated throughout all of time and everywhere on the worlds, for it is a truth from beyond them.</p>
<a href="#TABLE"> </a>

<p>The gods are people once like us who have come to such insight of the nature of Reality that they have gained some of the promised power of alteration. By their sheer will they may render and remake some parts of Reality. The gods of good aid those less enlightened, whereas the gods of evil use their superior knowledge to contort and control for their amusement and gain.</p>
<a href="#TABLE"> </a>

<p><a name="Rakah"></a></p>

<h2><a href="#TABLE">Chapter 3. &nbsp;&nbsp;&nbsp;Rakah and Darkness</a></h2>
Despite the fact that Emotion, Reality, Awareness, Other, Thought, Existence, Dreaming and Time had been created, there was still Darkness. However, it is always dark in the beginning. Don''t misunderstand me. It''s not like the darkness most beings currently dwelling in the Land are used to. It is not the sort of darkness where there comes no light forth, where the sun has gone under the far horizon for the night time, and the stars become visible in the sky. This was Darkness solely and alone because of the fact that there was nothing.

<p>&nbsp;</p>

<p>Worse yet, this Darkness had a mind, an evil mind, composed of all the nasty bits and pieces of all the yet created things, if one could call it evil. It wasn''t just evil, it was just indisposed towards everything but itself. Not that is ever encountered anything but itself, oh no! The Darkness thought itself to have always been, and always would be, undisturbed by anything else. The evil mind of Darkness couldn''t even grasp the concept of there being anything else but itself. Yet, suddenly, this time, the Darkness was in for a shock...</p>

<p>In this darkness the omnipotent being Rakah was created out of the good parts of Existence, Awareness and all the other entities to function as a counterpart to the Darkness. At first it was not much, alone in the Darkness a tiny pinpoint of light, surrounded in a halo, the one sole existing thing. It split the Darkness, it rendered it, it tore it to pieces with its unearthly light. The Darkness looked menacingly at the small pinpoint, growing more fearful and tense by the minute. Rakah, in the form of a small point, infuriated the Darkness even further, by moving randomly about with not a care in the world. Suddenly the Darkness reacted with violence, grew tall and big, surrounded the small point of light with its most dense darkness and tried to swallow it up, quench its light, extinguish it forever, so that the Darkness could go on to be, uninterrupted. But the Darkness dared not attack it head on, for it had a power which was unknown to it. The light grew, however. It grew in the form of a small cloud, with in the middle still that lightsource ever present. The cloud started slowly to expand and the lightsources in it became numerous. Slowly but inadvertently the Darkness was pushed aside, by the one thing, the only thing, that it hated, for indeed there was only one thing besides Darkness now, Light.</p>

<p>Henceforth I will call Rakah a she, but whether or not she was a she has never quite clearly come forward from the story. That part is assumed by the folklore of the people inhabiting this place, which of course isn''t all their fault. If you ask me, Rakah slipped up on that part while creating them. Well, that''s not all her fault, a lot of it they have to thank to themselves. But I must be getting back to my story.</p>

<p>Where was I? Ah, yes...</p>

<p><a name="First"></a></p>

<h3><a href="#TABLE">3.1 First Sense</a></h3>
<img align="left" alt="" src="http://www.geocities.com/Tokyo/5142/belldandy.jpg" /> Rakah, being newly born, however, didn''t have any of the senses the people nowadays have. She knew that she was there, that she existed, but she had no way to check this assumption. She wished to be able to have a notion of herself, feel her presence, and she created the First Sense, the Sense of Touch. Now she was able to feel herself rubbing against her. She knew now that she really felt good about herself. It immediately gave her a sense of coordination, which was severely practical. She noticed that she could move herself, twist herself into any shape she wished. During the first time after she created the Sense of Touch she had a lot of fun and possibilities changing and twisting her shape into all sorts of forms. It is even said that all the shapes that have ever come into the world since then, had all been a shape of Rakah in her practices with this Sense.
<p>&nbsp;</p>

<p>She could make herself very compact, and when she did change into a shape, immediately would the Darkness envelope her tiny self. Not attacking mind you, but abiding its time, for its time was sure to come.</p>

<p>The Darkness did try to win back some of the territory. For example when Rakah tried a very long stretch of herself just to see how far she could come and what it would be like. At that time, Darkness saw its chance and tried to push through Rakah right into the middle of that long stretch of herself. Rakah could feel the Darkness tugging and pushing slowly yet extremely strong and with all its strength in the middle of her, at that time, rather long body. The Darkness almost went through Rakah splitting herself into two separate parts that time. Before this could happen, though, Rakah sprang back into a large shape like a sharp spring and so prevented this story from an abrupt ending.</p>

<p>Rakah was a little more careful after that incident and she was henceforth more aware of the Darkness surrounding her then of old, yet still the Darkness would manage to thwart her plans, as we will see.</p>

<p><a name="Second"></a></p>

<h3><a href="#TABLE">3.2 Second Sense</a></h3>
<img align="right" alt="" src="http://www.geocities.com/Tokyo/5142/newurd.jpg" /> So now Rakah was aware of herself and from this point onward, there was a desire for creation, burning brightly within her. Rakah now could feel, but she couldn''t see what. Rakah looked about itself and saw nothing. Which wasn''t all that strange, seeing there was Darkness and everything, nobody could see a thing. This was clearly not very good, and Rakah created for itself the Second Sense, the Sense of Sight, in order to see what was going on. Along with the Sense of Sight, Rakah created, without apparently knowing about it, the things to see, white and black, colour, light and darkness. Finally Rakah got a good look at herself. She seemed to be made of a fleeting gas with all sorts of small floating dots of light in it. She could see where she would float past herself, she could see the dim points of light floating inside her. Finally she could see what it was all about.
<p>&nbsp;</p>

<p><a name="Third"></a></p>

<h3><a href="#TABLE">3.3 Third Sense</a></h3>
<img align="left" alt="" src="http://www.geocities.com/Tokyo/5142/skuld.jpg" /> Finally, she created the Third Sense, the Sense of Hearing. She didn''t do this out of sore need, not because she needed it or anything. It was just incomplete. She could feel what was happening, and she could see what was happening, but in order to bind these two together, to have a connection between the seeing and feeling, she needed a third sense. And at the same time she created sounds, without knowing it. She could hear herself, floating by, roaring by at great speed, and gently stopping. She could hear as well as see and feel the different points of light inside herself. For the first time in her comparatively short existance she could hear herself be. It was a small, thing, high sound, always in the distance and Rakah couldn''t exactly make out what it was. It was all around her, and, after closer examination, inside her too. She could almost feel the tone, and from this she became aware that life itself has a tone of its own, and each life has a different one. This gave her great joy.
<p>&nbsp;</p>

<p>''Tis said that this is the first time that she wished there were other beings around, so she could hear more tones, and perhaps these all together might make something other than that monotonous tone. This, wise people of Lore say, was the first time that Rakah had got the need to create something. Although I don''t know if this is true. (The Darkness ofcourse, had no tone whatsowever) <a name="Fourth"></a></p>

<h3><a href="#TABLE">3.4 Fourth and Fifth Sense</a></h3>
Finally, she got bored with all these senses. She could see from a distance, she could hear from a distance, the only sense which was a little more physical was the sense of feeling. She wanted more physical senses. So she created the Fourth Sense, the Sense of Taste. Now she could taste the darkness in front of her.

<p>&nbsp;</p>

<p>Finally, she created the Fifth Sense, the Sense of Smell. In order to supplement the Fourth Sense.</p>

<p><a id="Sixth" name="Sixth"></a></p>

<h3><a href="#TABLE">3.5 The Sixth Sense</a></h3>
Rumour has it, that she created one last sense. However, as the story goes, Rakah didn''t define this sense, and therefore it isn''t quite clear what this sense does. It appears that, as we see through history, people are born with a sixth sense which differs from all the other senses and as it isn''t defined this Sense takes various shapes in every being. Whether or not every being has a sixth sense is unclear, certain is that a lot of people have a sixth sense, and often the person in question has strange abilities on which other people look with an unfriendly eye, shunning the person in question. Therefore a sixth sense isn''t only a gift, it is also a burden to whomever has it.

<p>&nbsp;</p>

<p><em>First there was Darkness,<br />
Then there was Light,<br />
Rakah the being who exists,<br />
Was born there that Night.</em></p>

<p><em>Rakah wanted to feel around,<br />
So she created feeling.<br />
And she felt being unbound,<br />
And the First Sense gained its meaning.</em></p>

<p><em>Rakah was so very alone,<br />
So she developed Sight,<br />
To see what she was doing,<br />
And the Second Sense was borne into the night.</em></p>

<p><em>Rakah wanted to hear,<br />
So she made hearing,<br />
And it was to her very dear<br />
And the Third Sense went into being.</em></p>

<p><em>As encore Rakah created the last two<br />
as final supplement to all unabated<br />
The ability to smell she had to do<br />
And the Forth Sense was created.</em></p>

<p><em>The power of taste only was left.<br />
The last one of the five,<br />
And she created it with zest,<br />
And the Fifth Sense came to life.</em></p>

<p><em>''Tis said in lore,<br />
That she made one more.<br />
But that one is as yet unclear,<br />
Even to the greatest seer.</em></p>

<p><em>And so this sad song ends,<br />
For how it will continue, on us it all depends.</em></p>

<p><em>(Lay of the Five Senses) </em></p>

<p><a name="Genesis"></a></p>

<h3><a href="#TABLE">3.6 Genesis</a></h3>
She thought she had enough senses now and she quit with them and started to see what was out there. Apparently that wasn''t that much and she got very bored with herself, the only being in the entire Universe. What good were senses if there was nothing around to practice them with?

<p>&nbsp;</p>

<p><img align="left" alt="" src="http://www.geocities.com/Tokyo/5142/bellchan.jpg" /> Then she took from her body a single point of light, and threw it into the darkness in front of her. Immediately something strange began happening to the light, and under the influence of the thoughts of Rakah, it became bigger. This dot grew really big. Not that it amounted to anything like the shape of Rakah herself, but yet of a fair size. It was a sphere, totally white and giving off so much light that even Rakah had some problems looking into it. She looked at it, and she thought she had created the most beautiful thing ever to Grace her eyes. Which wasn''t all that strange, seeing it was the only thing she created yet.</p>

<p>But quickly her interest waned. The white sphere, which we now as Yasa, the Consumer, was very dull. Nothing was happening, you just could look at it, or don''t look at it. Rakah needed a new challenge, something that would evolve in time, Rakah sought something to divert her attention and took another of her tiny points of light in order to create a whole new concept. This point of light was a lot dimmer then the other one and it wasn''t the idea of Rakah to make another Sun. She made it grow like the sun, but to only a small size. When it reached the size she wanted it to have she dimmed the light even further, and the heat she was feeling from it became less intense, and then suddenly a crust developed around the sphere. After much tinkering with it and yet not bringing in too much detail, she sent it on its way around the sun.</p>

<p>After that she created the land, brown soil, and the water to fill the bigger gaps of texture of the planet, and the sky, air, and clouds of vapour, to wrap itself around the planet. The land and the water in it''s vastness didn''t have much detail then, nor where there any living things and the entire planet looked alone, desolate and rather rough around the edges. In the making of planets, Rakah had only a dim idea of what the idea was, and she thought to create details later, however, this didn''t happen because, without intention, she created something out of her own matter which would do it for her. After that creative process was over, out of her own matter, she created two shapeless forms. Then she took two points of light from herself and put each one in one of the shapeless forms. However, in one form she planted a fierce flowing point of light, piercing its tiny gas cloud thoroughly, while in the other one she put a smaller light, barely able to penetrate the gas cloud.</p>

<p>Then she created a small fireball, put in the two forms and send it slinging towards the planet. The fireball journeyed towards the planet, went through its thick atmosphere in one piece and crash-landed on a small clearing in the middle of the future Land of Karchan. The giant fireball came unto the clearing at a very high speed and crashed deeply into the soil, spilling out sand and earth in heaps all around it in amazing clouds. When the dustclouds lifted, a crater was visible. Not large, for it wasn''t such a large comet, yet the extreme heat that the comet generated fused the earth together, making it glasslike. The inside of that crater was still comparatively hot, yet was losing much of its heat in the cold air.</p>

<p>Where the comet had hit the ground, the ground was smooth, brown, very hard, and solid all round. At the bottom of the crater, when the dustclouds were gone, could be seen two forms or shapes looking around and that was when things started getting wrong.</p>

<p>Darkness was still out there and watched Rakah create the sun and the planet with contempt, waiting for a chance to thwart the plans of Rakah without success, but now, at last, its time had come. The Darkness created for the first, last and only time something. Something evil, something dark, something containing nothing but the Darkness itself, in its most compact form, It was a shard, a Black Shard, a shard made of solid Darkness. Now Darkness waited until a good opportunity would arise for it, to try and destroy everything Rakah created. However, the Darkness always took the sly route, the route of obscurity, so nobody would suspect that the Darkness was behind it. And when Rakah was looking at the place on the planet where the two shapeless forms were put, the Darkness took its chance, and managed to get on the other side of the planet, the dark side. And quickly, before Rakah could interfere, the Darkness enveloped the planet for a few seconds and disappeared again. During those seconds the Shard was inserted into the least bright form by the Darkness, and the Darkness disappeared again without Rakah, and the two nameless shapes, knowing what had happened. But the Shard stuck in the form, throbbing like a bad evil heart, and could not be detected, ready to start its evil work, to change the shape and bring it over to the Darkness, so the Darkness would have a servant on the planet, ready to do its evil bidding.</p>

<p><a name="KarnZahnos"></a></p>

<h2><a href="#TABLE">Chapter 4. &nbsp;&nbsp;&nbsp;Karn and Zhanos</a></h2>
In a small clearing in the middle of the land, grass-grown and surrounded by trees, the two shapeless forms were beginning to come to life, the small points of light lighting up strongly with white light within them. They were still gas-like and had no solid shape yet. In the beginning they were made of the same matter as Rakah, but they discovered that that matter was governable into any form or type they wanted, as indeed Rakah found out herself. They were now also capable of forming themselves into any shape they wanted. They both became very different shapes in all sizes and after some experimenting, they tried to agree on the shape that they would assume for most of the time. A shape that was agreeable to them both, and they found out a shape, consisting of a large body, with four cylindrical shapes coming out of it and a small sphere on top. After a while, and much shifting of matter, they managed to make something out of themselves.

<p>&nbsp;</p>

<p>The two beings called themselves Karn and Zhanos, and they had, just as Rakah, the power to create and to destroy. They looked at the land and both thought it a desolate place. But while Zhanos liked it, Karn sought to improve it with things that would enliven it a bit. Zhanos was not at all pleased at this, as he liked the desolation and all and loathed changing it.</p>

<p>Karn wanted to create things and Zhanos wanted to undo them. At last Karn could take it no longer, and formed some vocal cords. And then they created, between themselves, the first fight.<br />
''Why do you say that this desolate place should remain the same?'', spoke Karn for the first time, with a high clear sound as the sound of a knife gently being stricken against a crystal glass. Zhanos was shocked at the new development, also formed some vocal chords, less adept then Karn but still functional.<br />
''Because I like it the way it is, and I don''t want you to meddle with it!'', said Zhanos with a harsh and particularly unpleasant sound. Karn sighed.<br />
''But what do you want from me? I mean, look at this place, it is totally clear that Rakah meant us to do something with it, in order to amuse her.''.<br />
''I am telling you to leave it the way it is, it is nice and desolate, and I like it!! If you start changing it again, I''ll have to try and stop you!''.</p>

<p>At this, Karn was shocked yet his brow darkened and his face stood resolute, not bent on giving in into the whims of his brother.<br />
''Well, then, maybe you should go ahead and try!'', said Karn to Zhanos. And then commences the very first fight, and one of the best, ever told in the entire history of Karchan, it was also the last time that two beings of such terrible powers came in contact with each other. The exact choreography of the fight is not given here, as it reaches far above our concepts of fighting. Some people say that they used their vast minds to trick each other, and that the entire battle took place between them without moving. And if that is what you want to believe, then go ahead because I am not about to write anything down to try and mar this great event with words that are totally inadequate.</p>

<p>Oh, well, where was I?</p>

<p>&nbsp;After a particularly long while of continuous battle, they stopped fighting. Both in extremely good health and both unable to get the other one to yield. Apparently, they both were made of the same stuff, and were completely equal to each other. There was no difference between the two, except maybe in their minds. And as they were exactly the same, they were an exact match for each other. It was impossible for one to triumph over the other. But then Rakah, seeing that the amusement had stopped and that there never would be a conqueror, she choose between Karn and Zhanos. And she choose Karn. Creating a being not unlike Karn and Zhanos to serve as her messenger; Fate.&nbsp; Immediately they were made aware of the choice that was made by Rakah, and Zhanos turned aside, his expression horribly changed in a mix of hatred against Rakah and Karn, and surprise, and the knowledge of the fact that he wasn''t chosen. This bitter memory he cherished forever and from this point on his hatred never diminished or increased, it was constant and eternal. He walked away from the clearing that day and withdrew far away in the rocky mountains in the South of the land, never to be seen for a long time. Then, the night fell, and the sun disappeared over the horizon.</p>

<p><a name="Sunrise"></a></p>

<h3><a href="#TABLE">4.1 Sunrise</a></h3>
Karn and Zhanos were separated and the night approached, and darkness was everywhere. Not the Darkness talked about in the above chapters, but this was normal average peaceful darkness and Karn knew he had nothing to fear, lay himself down on the grass and waited for the sun to come up again, but after a quarter of an hour a very strange thing happened, Karn lost consciousness. At last the Sun rose in the east, at first a small glint of yellow was visible in the east. Then, a small portion of the Sun became visible shooting rays of light all over Kúri. A few of these rays of light, gently stroked the face of Karn, and he awoke, yawning, and saw a sunrise for the very first time of his life. He was speechless and watched the Sun ride into the sky, lighting every colour in the scenery around him more perfect than he had remembered it the other day. He just sat there and watched the Sun rising slowly into the sky, he watched the clouds in the sky slowly turning nice white, he watched the stars disappear slowly into a nice.. <a name="Gods"></a>

<h2><a href="#TABLE">Chapter 5. &nbsp;&nbsp;&nbsp;Creation of the Gods</a></h2>
Karn and Zhanos were entirely alone on the face of the planet. They didn''t get along, as recounted in the previous chapter of this History and Zhanos went away, bitter and angry, and travelled far into the mountains which surrounded this desolate valley. Karn, after watching with a heavy hart Zhanos disappear into the twilight, sat down onto the grass-covered ground sighing. ''I want to develop this land, and when I''m through you''ll never recognise it.''. Karn spoke to himself, ''However, in my current form, I cannot do much.''. So he made up his mind to divide his mind equally among all the things to be done. And low and behold, before the eyes of Karn, the thoughts of every aspect that was needed came to life, and they were formed in the likeness of Karn. Karn, after realising what he had done, gave them names.

<p>&nbsp;</p>

<p>And Gods to reign all over the world and shape it after the way he thought it was meant to be. All these Gods were named by the Elves, and their names are here given. What the original names of the Gods were, or whether they had names or not isn''t known. Among these Gods were Atalaya, Goddess of Every Living Thing, guardian of all that lives and breathes upon Kúri, Manatoba, The Smith of the Gods, who could make anything for them, Mielikki, Queen of the Forests, who covered the Land in lush and growing things, Meadel, King Under the Sea, who was in charge of the ocean and is the guardian of all Ships, and Havens, and Ports, and see-faring people in general, Furchin, God of the Land, guardian over mountains, hills, valleys, and every other sort of landscape.</p>

<p>''I greet you all, my Gods. I have created you after my own image, so you can do my bidding.'', Karn spoke to them. Manatoba was the first to speak.<br />
''And what is you bidding, father?''.<br />
Karn was a little taken aback, he never considered himself a father, but he was now, at least in the eyes of his children.<br />
''My bidding is to you to develop the world you see around you as you see fit. I have all the confidence in you I need, for you are of me, and have the same ideas as I have, and therefor you will always do that which I feel to be good.'' The Gods nodded.<br />
''I have to leave now, and leave you.''<br />
''Why must you leave?'', Atalaya spoke with a clear and beautiful voice, ''Why cannot you stay, and help us, guide us, give us advice as to what it is we should do?''<br />
''I have no advice to give you, all the ideas you have are already shaped inside your minds, ideas I put there, and I wish of you to make them come true in this world.'' At this, Karn turned around and walked away. When he was at a fair distance, he turned round once more, and said in a clear voice :''Fear not, for you have nothing to fear. I will not be gone far, and wherever you go, I will always be within hailing range.''</p>

<p>Karn left them and disappeared into the mountains, as Zhanos had done previously, and if, by any chance, you might see an old man, clad in rags, sitting beside the road, take care and don''t act foolish. Atalaya, Manatoba, Mielikki, Meadel and Furchin talked about what they were about to do, and it was agreed by all that Furchin, should take care of the making of a place for the Gods, where they could rest and live. And Furchin journeyed with the other Gods to the shore of the Great Ocean, and commanded the ocean floor to rise above the waves, and low and behold the ocean floor moved, and a great earthquake came. The ground shook with rage, and all looked at Furchin in awe of his power of creation. Below the waters a great brown shape appeared, rising steadily until it reached the surface of the ocean. Above the waters, the land rose, and rose, and a fairly big island became visible. Then Atalaya stepped forward, raised her hands, and a veil of illusion came down around the island, to keep it hidden from all creatures that had no business there. Next in line was Meadel, King of Water, and he let the waves break upon the soil, creating by their compound impact a long beach on the south side of the Island, and dozens of fjords on the west-side. Manatoba pushed forward and laid on the island roads of pure white stones, shimmering in the sunlight.&nbsp; Mielikki cast beautiful flowers as the world has never again seen to grow wherever they rest, covering this place in such beautiful colors and greenery that all after would pale by comparison. Then The Gods made their place there, build their own houses, which do not have their equal among the palaces of men and elves upon the earth.&nbsp; And after this great work was done, they rested for a while upon this Island, each in their own home.</p>

<p>In the middle of this Island, a mountain could be seen, and none of the Gods occupied it. And Manatoba called to the Gods, that he would make a meeting place there, for all the Gods. And he created a place, unrivalled among all the houses of the Gods, huge, and visible all over the Island. Their first real meeting was held there.</p>

<p><a id="InTheMeanTime" name="InTheMeanTime"></a></p>

<h2><a href="#TABLE">Chapter 6. In the mean time...</a></h2>
In the meantime Zhanos was always working, far beneath the mountains. He had heard and seen the things that were going on in the valley where Karn resided. He had witnessed the creative process. And now there was only one thing to do. He also created Gods, evil Gods, Gods to make the world a living hell. And among these gods were Osnodon, Lord of the Underworld, to this world, all beings go that die and he is the exact opposite of Atalaya and to whatever creature Atalaya gave her gift of life, so Osnodon gave his ''gift'' of death, except one, but that is another story. Dagorii, Lord of Battle, probably the most warlike god ever to roam around the world, and last but not least, Kulanin, God of Shapes, who had the power to change into any form he wished, and his descendants have the same ability in more or lesser degree, and throughout the history of the Land of Karchan, his deeds have been evil, and destructive wherever he went, for it is said that Zhanos himself trained him in the art of deception, the art of slander, and the art of manipulating people in general.

<p>&nbsp;</p>

<p><a id="CreationOfRaces" name="CreationOfRaces"></a></p>

<h2><a href="#TABLE">Chapter 7. Creation of Races</a></h2>
The Gods were working day and night in order to populate the Land of Karchan, Manatoba would build the different races, using the powers of Furchin and Mielikki as building blocks, Atalaya was to bring life to these different beings and creatures.&nbsp; There was much chaos in the beginning, as these beings came to clash as often as Zhanos and his ''Sons'' could get them to.&nbsp; To many this time is also called The First War.

<p>&nbsp;</p>

<p><a id="ThreeBooks" name="ThreeBooks"></a></p>

<h2><a href="#TABLE">Chapter 8. The Three Books of Evil</a></h2>
As Zhanos grew mightier among his Gods, he wrote three books. Of these three books, was explained the source of evil and how people could master it. These three books were evil themselves, every creature that even came near those books was able to feel their cold, aggressive, and evil nature. These books were written by Zhanos in order to lead many good men, and other folk into either doom or the grasp of the Dark Lord.

<p>&nbsp;</p>

<p>It is said that in the First War these books were lost, but a strange inscription written by a traveling scholar was donated to the library in Pendulis in the recent era that claims the books were purposely dispersed.&nbsp; Some things even I cannot see.<a id="FirstBook" name="FirstBook"></a></p>

<h3><a href="#TABLE">Chapter 8.1 The Book of Power</a></h3>
Among these books was the Book of Power, the most important book, and the one that gave Zhanos the most trouble in writing. This book was essential in order to be able to read/use/understand the other two volumes, and still powerful on its own.&nbsp; This was given to Dagorii, that he might lead mortals to struggle for power; the ultimate corruptor.

<p>&nbsp;</p>

<p><a id="SecondBook" name="SecondBook"></a></p>

<h3><a href="#TABLE">Chapter 8.2 The Book of Wrath</a></h3>
Among these books was the Book of Wrath, this book was destined to be used for the total unleashment of emotions like anger, agression, and other evil thoughts, and would condemn a man to the same level as Zhanos, to be his eternal servant in evil.

<p>&nbsp;</p>

<p><a id="ThirdBook" name="ThirdBook"></a></p>

<h3><a href="#TABLE">Chapter 8.3 The Book of Destruction</a></h3>
The Book of Destruction was a work, written by Zhanos solely for the purpose to do as much damage as possible; to annihilate, if possible, everything Karn has wrought.

<p>&nbsp;</p>

<p><a id="Downfall" name="Downfall"></a></p>

<h2><a href="#TABLE">Chapter 9. Downfall of Zhanos</a></h2>
In the time after the First War, after Zhanos had been so surpisingly defeated in His seemingly fool-proof plan for the pre-emptive destruction of the races of the world, there came a great age of peace and unity among the peoples of the world and they began to share a goodly way of life.&nbsp; In this era, named after as The Age of Civilization (AC), mankind began to forge relationships with the elves and dwarves that they initially had sought to destroy.&nbsp; Together they built great kingdoms and carved beautiful cities out of the Land.&nbsp; Many began to worship Karn''s Deities, seeking and receiving favor and magical ability.<br />
<br />
Zhanos was defeated, but not destroyed, and his hatred for all things burned as strongly as it always had.&nbsp; He schemed anew, this time with experience on His side more than ever before.&nbsp; He plotted.&nbsp; He commanded.&nbsp;&nbsp; Soon new horrors as the world had never before seen came into being.&nbsp;
<p>&nbsp;</p>

<p>It is known that evil cannot create, only destroy, and indeed these new monstrosities were the result of the Touch of Evil upon mortal souls, for all the hapless creatures enslaved by Zhanos'' Thauranko were forced into grueling labor in the blight of Darkness, or twisted into things unnatural.&nbsp; Most notably of these perversions are the bloodlust-filled <em>Metguul</em>, The Lich Kings, and <strong><em>Uliimathroqu</em></strong> (The Devourer of Thrones). &nbsp;<br />
<br />
The souls of mortals possessed by those of Dagorii''s daemonic and formless children, when bound to mortal creatures in unholy ritual, became what some call vampyr and others vampire, though neither are right.&nbsp; Osnodon, Lord of Death, found among the captives from the Sunlit Lands some who were indeed slaves, but willing slaves, devoted deliverers of his twisted Curse upon mortalkind.&nbsp; To these few he granted Unlife that gave them powers over the husks of those that had died, and through them the zombie, vampire, Skeleton and other such desecrations of the Undead have come to be known. &nbsp;</p>

<p>Most terrible of these workings was that of Kulanin who deceived and seduced Dagorii''s most loyal High Priestess, an elven maiden from the First Family that had betrayed her kin for love of evil, and she bore unto him a beast of such unspeakable horror and destruction that even Zhanos was appaled and Kulanin smote it''s name and banished it the depths of Kuri where none would cross it until the end of all time, when the beast''s name will be remembered and it will devour the world...<br />
<br />
If you''ll excuse me, I always get a bit sad at that part.&nbsp; Where was I? &nbsp;<br />
<br />
So Zhanos schemed, and His servants crafted, and all too soon He was prepared to destroy the new and great world that mortals had been making in His absence.&nbsp; He summoned His sons, the Thauranko, to Him in His dark castle on the tallest fell mountain in the lands on the now Dark Side of the world.&nbsp; Up there, where there was no air and no light nor the other things all creatures on Kuri need for life, He told them of His plan.&nbsp; They were surprised, for His plan would not fail this time, and they knew it instantly upon hearing it.&nbsp; This time all things would come to be destroyed, Forever.&nbsp; Soon, He would not need even they his Sons, and then Karn would go, and then Himself, for this is what the Shard of Darkness deep within His being drove Him to do. &nbsp;<br />
<br />
The Thauranko exchanged a glance, a meeting of minds, and a decision was made.&nbsp; The war began, and again the nature of mortalkind nearly drove them to the destruction of themselves, when again something amazing happened.&nbsp; Two things actually. &nbsp;<br />
<br />
One thing, was that the God of Will, of Fire and of Freedom, sacrificed his godly essence for mortalkind to be free to craft their own Fates through the use of Magic.&nbsp; The more suprising thing, and even as Zhanos became aware that it was going to happen, the Thauranko attacked Him as one and together His sons betrayed Him and tore from Him His essence; the Shard of Darkness, taking it far away from Him and thusly striking him of much of His apocolyptic power. &nbsp;<br />
The corruption on His being was irreversable, but the diminishing of His power had made Him less than Godly, and so it was that he was exiled by his own children, to walk the sunlit lands as an Immortal, a Lesser God, thus insulting him moreso than even Rakah had by choosing Karn over Himself. &nbsp;<br />
<br />
Hereafter, Zhanos was no longer the true ruler of the Throne of Evil, and His power was divided among His Thauranko.&nbsp; Therefore, the Immortal and ever evil creature that once was Zhanos is ever after refered to as Zahnos when he reappears in History to strike at the workings of good and set vengence upon his Sons.<br />
This ended the Second Great War, and time thereafter began in the Year of 0 of the Age of Mortals (AM), and the Age of Civilization was mostly forgotten.&nbsp;</p>

<p>&nbsp;</p>

<p><a id="Shard" name="Shard"></a></p>

<h2><a href="#TABLE">Chapter 10. The Tale of the Shard</a></h2>

<p>&nbsp;</p>

<p>&nbsp;</p>

</div>');
replace into templates 
(id, name, created, modified, content)
values(13, "chronicles/people", now(), now(), '<div class="container">
<script src="/javascripts/charactersheets.js" type="text/javascript"></script>
<p>I feel the following needs a little explanation. Below you see a list of available Character Sheets. They contain personal information like name, title, place of birth, and the story line of characters, and references to other characters. In each case these are put together by the people that originally created the character on the game.</p>

<p>It provides valuable insights into the story behind this Game.</p>

<p>Now you can add your piece of information as well. Just fill in your name and password of the character you created on the mud, and you will be presented with a form that you can fill out, and change later in the same way.</p>

<div id="karchan_charactersheets">Loading content. Please hold...</div>
</div>
');

replace into templates 
(id, name, created, modified, content)
values(14, "chronicles/fortunes", now(), now(), '<div class="container">
<script type="text/javascript" src="/javascripts/fortunes.js"></script>
<p>This list contains the most rich people in the Land of Karchan.</p>

<div id="karchan_fortunes">Loading content. Please hold...</div>
</div>
');
replace into templates 
(id, name, created, modified, content)
values(15, "chronicles/guilds", now(), now(), '<div class="container">
<script src="/javascripts/guilds.js" type="text/javascript"></script>
<p>Click on the little blue dragon in front of the guild name, to show more information about a certain guild.</p>

<p>Click on the title of a Guild to be redirected to the website of the guild.</p>

<hr />
<div id="karchan_guilds">Loading content. Please hold...</div>

</div>
');

-- Help --

replace into templates 
(id, name, created, modified, content)
values(16, "help/index", now(), now(), '<div class="container">
<p>This page contains the central help page, along with a FAQ (Frequent Asked Questions) and is dedicated to keeping the game in proper working order.</p>

<p>Use these pages to find out the latest news on the state of the game, and even to help make the game better. I might especially like to draw attention to the <em>Bugs</em> option.</p>

<p>If you are more interested in the background/history/story/fairytale in the mud, please check out the <em>Chronicles</em> webpages which can be found one level higher.</p>

<div><em>Karn (Ruler of Karchan, Keeper of the Key to the Room of Lost Souls)</em></div>
</div>
');
replace into templates 
(id, name, created, modified, content)
values(17, "help/status", now(), now(), '<div class="container">
<script src="/javascripts/status.js"></script>The game is currently online and ready to be played.
<h2>Deputies</h2>

<p>These are the current active deputies:</p>

<div id="karchan_status">&nbsp;</div>
<script>
function showStatus()
{
  if (window.console) console.log("showStatus");
  $.ajax({
    type: ''GET'',
    url: "/karchangame/resources/public/status", // Which url should be handle the ajax request.
    cache: false,
    success: (function(data) {updateStatus(data); }),
    error: (function() { alert("An error occurred. Please notify Karn or one of the deps."); }),
    complete: (function() { if (window.console) console.log("complete"); }),
    dataType: ''json'', //define the type of data that is going to get back from the server
    data: ''js=1'' //Pass a key/value pair
  }); // end of ajax

  var updateStatus = function(data) {
    if (window.console) console.log("updateStatus");
    // The data parameter is a JSON object.
    var formatted_html = "";
    for(i=0; i<data.length; i++)
    {
         formatted_html += "<p>* " + data[i].name + ", " + data[i].title + "</p>";
    }
    formatted_html += "";
    $(''#karchan_status'').html(formatted_html); // data.products);
  } // updateStatus
}

$(document).ready(function() {showStatus();});</script>
</div>
');
replace into templates 
(id, name, created, modified, content)
values(18, "help/guide", now(), now(), '<div class="container">
<p><a name="preface"></a></p>

<h2><a href="http://www.karchan.org/karchan/help/help.html#TABLE">Preface</a></h2>

<p>Welcome to the General Guide of the mud. Here you will find an explanation of the most current commands as well as how to live and prosper in the land and enhance your character. Please bear in mind that this guide is a work in progress and as such is subject to constant change.</p>

<p><em>Disclaimer</em></p>

<p><em>Neither me (Karn), not the deputies are responsible for the players in the game. All we provide is the architecture/medium or whatever you call it to communicate with other players, play the game, or engage in RolePlaying.</em></p>

<p><em>This means that it is quite likely that you will meet other people that are either rude, lewd, offensive, disgusting or unpleasant in other ways. If they behave in this way directly towards you, you may ask them to stop it. If they fail to comply, this is called ''harassment'' and is grounds for banishment from the game. You can request help from a deputy in this case. However, if people are unpleasant in an indirect way you can either request that they leave the room and continue someplace else, or you can yourself leave the room. This is not ground for banishment.</em></p>

<p><a name="chap1"></a></p>

<h2><a href="http://www.karchan.org/karchan/help/help.html#TABLE">Chapter 1. How to do What</a></h2>

<p><a name="chap11"></a></p>

<h3><a href="http://www.karchan.org/karchan/help/help.html#chap1">Moving Through the MUD</a></h3>

<p>You can go west, east, south and north, and that is it. If you can''t pass at one time it is possible that the way is blocked. Find something to free it and you will be free to travel anywhere you like. Do not try anything like <strong>move to the bar</strong> or <strong>move east</strong> or <strong>go to the shop</strong> because this will never work. If it does work, it will be because there are right and left directions as to how it should be done.</p>

<p>One more thing, if by any change, you succeed in doing something special, like open a hidden doorway, it is of no use to you to use the words <strong>move towards doorway</strong>, what you should do, is look carefully at the thing you have just places, or pulled, or opened, and in the text will be written that there is <strong>a way up or down, east, north, south or west</strong>. That is the way and the only way to do it. <a name="chap12"></a></p>

<h3><a href="http://www.karchan.org/karchan/help/help.html#chap1">Communication</a></h3>

<p>It isn''t very hard to communicate to one another. There are a few simple commands that detail this. (<em>See</em> <a href="http://www.karchan.org/karchan/help/help.html#List">List of all possible commands</a>) A third party can hear what you say to somebody else. If you whisper, however, that will not be the case. He will hear that you are whispering something, but he can''t make out what it is.</p>

<p><strong>One more thing</strong>: It is a tricky bussiness to talk to the ''Bots. Bots are people that are a fixture in this game. They are not real. They should be able to do simple tasks and you can ask them somethings or say things to them, but it is very likely that they won''t say something back. However, I (The maker of this MUD) update the responses of every character every few hours, so if at one time he doesn''t answer any of your summons, perhaps the next day he will. Important is it that you always talk <strong>to</strong> a fixed character. Like this: <strong>say to <em>Bill Hello</em></strong> and this will ilicet some sort of response. Don not use any quotes like '' or " and no use of questionmarks (like ?) or exclamationmarks (like !) or , or . or whatever, as that will illicet no response whatsoever. And by the way, short sentences will always work better than long ones. <a name="chap13"></a></p>

<h3><a href="http://www.karchan.org/karchan/help/help.html#chap1">How to get Money</a></h3>

<p>Try selling things to the shopkeeper. You will get a certain amount of copper, silver and gold coins for your trouble. If you type <strong>list</strong> you will be able to see what can be bought. If you have any more question, try reading the sign in the shop. It is a small discription of how you should operate within the shop. <a name="chap2"></a></p>

<h2><a href="http://www.karchan.org/karchan/help/help.html#TABLE">Chapter 2. Quests</a></h2>

<p><a name="chap21"></a></p>

<h3><a href="http://www.karchan.org/karchan/help/help.html#chap2">Normal Quests</a></h3>

<p>Normal quests are used in the game. They are usually created by one specific deputy and they are generally quite short and are easily to solve. Some general rules to solve most quests:</p>

<ol>
	<li>look at things in rooms, they usually carry descriptions that mean something (for example ''look at chest'')</li>
	<li>search things, especially usefull if you have looked at them and seem to see something odd (for example, ''search road'')</li>
	<li>when looking at things, sometimes the description tells you what to do. Some rooms have special commands that will only work in that room with a combination of certain items that you have etcetera.</li>
</ol>

<p>Solving of a quest generally means that you gain a certain number of experience points and perhaps even so many experience points that you level. <a name="chap22"></a></p>

<h3><a href="http://www.karchan.org/karchan/help/help.html#chap2">Special Quests</a></h3>

<p>A Special Quest is a Quest that effects Every/Most Player in the Game! As such these special quests are carried out in possible combination between me (Karn) and some/all of the deputies. It is usually more complicated then a normal quest and is usually extended throughout the game area.</p>

<p>A good example of a special quest is/was <em>The Whiteblodge Plague</em>. <a name="chap23"></a></p>

<h3><a href="http://www.karchan.org/karchan/help/help.html#chap2">The Quest</a></h3>

<p>What would a MUD be if it didn''t have some deeper meaning behind it? What people on this MUD need is a goal, something to strive for, something to achieve. Well, that goal is there, that goal is becoming a wizard.</p>

<p>How do you become a wizard? Very good question, well, a wizard needs a book of magic.</p>

<p>Right now it is still impossible to retrieve the Book of Magic, seeing as I haven''t finished implementing it yet. Come back for this guide at a later date when it has been updated.</p>

<p><a name="chap3"></a></p>

<h2><a href="http://www.karchan.org/karchan/help/help.html#TABLE">Chapter 3. Netiquette</a></h2>

<p>Netiquette is what you behave like on this Mud, or anywhere else on the Internet in general. The following rules could be considered as guidelines:</p>

<ol>
	<li>Always make sure that people can understand you.</li>
	<li>Do not use abusive language.</li>
	<li>Never ever do anything to people, if they do not want you to.</li>
	<li>Don''t type anything which somebody has asked you to type, unless you are very sure of yourself.</li>
	<li>Do not make fun of people.</li>
	<li>Do not harass people</li>
</ol>

<p><a name="chap31"></a></p>

<h3><a href="http://www.karchan.org/karchan/help/help.html#chap3">The Law</a></h3>

<p>Yes, there is a <em>Law</em> in the land of Karchan. Every player as well as the deputies and me has to try and be true to this law. The law(s) can be found at <a href="http://www.karchan.org/karchan/help/thelaw.html">The Law of Karchan</a>. Remember, ignorance of the law is no excuse.</p>

<p><a name="chap32"></a></p>

<h3><a href="http://www.karchan.org/karchan/help/help.html#chap3">The Knights of Karchan</a></h3>

<p>The Second party in command of this mud is the Knights of Karchan, they are the one that roam the land all day and have a special room in the castle. They have the ability to kill or hurt people vehemently if they cross the thin line between mildly annoying and vehemently harassing. <a name="appA"></a></p>

<h2><a href="http://www.karchan.org/karchan/help/help.html#TABLE">Appendix A. Terminology</a></h2>

<p>Over the years, some players of the game have started to develop their own abbreviations in dayly Karchanian life. For some of these, nobody knows where the term originally came from, for most of them they are closely related with approximately the same terms as used in other muds.</p>

<dl>
	<dt>AFK</dt>
	<dd>short for <strong>A</strong>way <strong>f</strong>rom <strong>K</strong>eyboard</dd>
	<dt>brb</dt>
	<dd>short for <strong>B</strong>e <strong>R</strong>ight <strong>B</strong>ack</dd>
	<dt>HTML</dt>
	<dd>HTML, <em>H</em>yper<em>T</em>ext <em>M</em>arkup <em>L</em>anguage, designed to make life easier and internet more interesting. It uses commands to design text-files (or better said HTML-files) with different fonts and text-styles and images. Every command commences with a &lt; and ends in a &gt;. If you are using Netscape you should be able to see the different commands in this file. Everything is proparly explained in <a href="http://www.il.fontys.nl/%7Emaartenl/htmlprim.html">this link</a>. Important to know is that in this game of mine, communication can also use special commands from HTML.</dd>
	<dt>Huskies</dt>
	<dd>persons who do not type quit and still shutdown their browser/internet connection but stay active.</dd>
	<dt>Karchanian</dt>
	<dd>persons who have played or are playing the game are referred to as Karchanians</dd>
	<dt>Lag</dt>
	<dd>slow responses, due to a bad and slow connection.</dd>
	<dt>LL Karchan</dt>
	<dd>short for <strong>L</strong>ong <strong>L</strong>ive Karchan. A lot of other combinations can be made, like <em>LL Karn</em>, <em>LL Buttered Toast</em>, etc.</dd>
	<dt>Morphies</dt>
	<dd>persons who can change their sex and appearance.</dd>
</dl>

<p><a name="appB"></a></p>

<h2><a href="http://www.karchan.org/karchan/help/help.html#TABLE">Appendix B. List of all possible commands</a></h2>

<dl>
	<dt><a name="gowest">go west/west/w</a></dt>
	<dd>will make your character move to the room in the west if that is possible.</dd>
	<dt><a name="goeast">go east/east/e</a></dt>
	<dd>will make your character move to the room in the east if that is possible.</dd>
	<dt><a name="gonorth">go north/north/n</a></dt>
	<dd>will make your character move to the room in the north if that is possible.</dd>
	<dt><a name="gosouth">go south/south/s</a></dt>
	<dd>will make your character move to the room in the south if that is possible.</dd>
</dl>

<p><em>Communication</em></p>

<dl>
	<dt><a name="say">say &lt;something&gt;</a></dt>
	<dd>will make your character say something which can then be heard by everybody which is at that time present in the room.</dd>
	<dt><a name="sayto">say to &lt;person&gt; &lt;something&gt;</a></dt>
	<dd>will make your character say something to a certain person which can then be heard by everybody which is at that time present in the room. The person will see that you are talking to him.</dd>
	<dt><a name="shout">shout &lt;something&gt;</a></dt>
	<dd>will make your character shout something which can then be heard by everybody which is at that time present in the room.</dd>
	<dt><a name="shoutto">shout to &lt;person&lt; &lt;something&gt;</a></dt>
	<dd>will make your character shout something to a certain person which can then be heard by everybody which is at that time present in the room. The person will see that you are shouting to him.</dd>
	<dt><a name="ask">ask &lt;something&gt;</a></dt>
	<dd>will make your character ask something in general which can then be heard by everybody which is at that time present in the room.</dd>
	<dt><a name="askto">ask to &lt;person&lt; &lt;something&gt;</a></dt>
	<dd>will make your character ask something to a certain person which can then be heard by everybody which is at that time present in the room. The person will see that you are asking him something.</dd>
	<dt><a name="whisper">whisper &lt;something&gt;</a></dt>
	<dd>will make your character whisper something softly which can then be heard by everybody which is at that time present in the room. Use <strong>whisper to</strong> to talk to somebody without running the risk of somebody easvesdropping.</dd>
	<dt><a name="whisperto">whisper to &lt;person&lt; &lt;something&gt;</a></dt>
	<dd>will make your character whisper something to a certain person. The person will read what you whispered to him. Important to know is that fact that, though nobody else can hear your conversation, they do know that you are whispering</dd>
	<dt><a name="tellto">tell to &lt;person&lt; &lt;something&gt;</a></dt>
	<dd>will make your character create a mental link with somebody. This means that you can communicate unheard and across many rooms. This features is also handy to see if someone is active in the game.</dd>
	<dt><a name="fight">fight &lt;person&lt;</a></dt>
	<dd>will make your character start fighting with a person that is in the current room. This is only possible if the person in question is allowed to be attacked. Both of you have <em>pkill</em> set to "on" if both of you are regular players and wish to fight. The screen will show the progression of the battle. (See also <em><a href="http://www.karchan.org/karchan/help/help.html#pkill">pkill</a></em>, and <em><a href="http://www.karchan.org/karchan/help/help.html#stopfighting">stop fighting</a></em>)</dd>
	<dt><a name="stopfighting">stop fighting</a></dt>
	<dd>will make your character stop fighting. This does not mean that the person attacking you will stop. (See also <em><a href="http://www.karchan.org/karchan/help/help.html#fight">fight</a></em>)</dd>
	<dt><a name="whimpy">whimpy &lt;string&lt;</a></dt>
	<dd>this is a safeguard to make sure that you are not killed while you weren''t looking. It is a boundary. If your vitals go across this boundary, which can be set with the command <em>whimpy</em>, you will automatically run away as fast as you can from the blackguard who is killing you. Normally it is set to Not whimpy at all. In order to set the standard at <em>Not whimpy</em>, just use the command <em>whimpy</em> without the parameter.<br />
	Possibilities are:
	<ol>
		<li>feeling very well</li>
		<li>feeling well</li>
		<li>feeling fine</li>
		<li>feeling quite nice</li>
		<li>slightly hurt</li>
		<li>hurt</li>
		<li>quite hurt</li>
		<li>extermely hurt</li>
		<li>terribly hurt</li>
		<li>feeling bad</li>
		<li>feeling very bad</li>
		<li>at death''s door</li>
	</ol>
	</dd>
	<dt><a name="meflippo">me &lt;something&gt;</a></dt>
	<dd>will make your character do something which you have to type. This is very handy indeed if you want to express an emotion which isn''t in the games standard commandstructure. Use it like this: <em>me is going to get some coffee.</em>. Everybody in the room, including you, will receive the following message : <em>Karn is going to get some coffee.</em>.</dd>
	<dt><a name="look at">look at &lt;something&gt;</a></dt>
	<dd>this is extremely handy and very <strong>important</strong>. This way you can examine objects during the game more closely. In this way you will get a lot of extra information about something. Look at everything you encounter. It will help!</dd>
	<dt><a name="read">read &lt;something&gt;</a></dt>
	<dd>This will read something to you.</dd>
	<dt><a name="open">open &lt;something&gt;</a></dt>
	<dd>This will make your character open something. This will only work if you can open it, if is isn''t locked, and if it isn''t open already. Usually you should be able to see in the images that the thing you opened is indeed open. (<em>See</em> <a href="http://www.karchan.org/karchan/help/help.html#close">close</a>)</dd>
	<dt><a name="close">close &lt;something&gt;</a></dt>
	<dd>This will make your character close something. (<em>See</em> <a href="http://www.karchan.org/karchan/help/help.html#open">open</a>)</dd>
	<dt><a name="inventory">inventory/i</a></dt>
	<dd>will show a a full list of all the items you are carrying and the amount of gold, silver and copper coins you possess. (<em>See</em> <a href="http://www.karchan.org/karchan/help/help.html#search">search</a>, <a href="http://www.karchan.org/karchan/help/help.html#drop">drop</a>, <a href="http://www.karchan.org/karchan/help/help.html#get">get</a>)</dd>
	<dt><a name="get">get &lt;something&gt;</a></dt>
	<dd>this will make your character get something from the ground which is lying there. You should be able to see what, if you use the command ''look around'', however, to find hidden items, see <em>search, drop, inventory</em>.</dd>
	<dt><a name="eat">eat &lt;something&gt;</a></dt>
	<dd>this will make you eat something from your inventory.</dd>
	<dt><a name="drink">drink &lt;something&gt;</a></dt>
	<dd>this will make you drink something from your inventory.</dd>
	<dt><a name="destroy">destroy &lt;something&gt;</a></dt>
	<dd>this will destroy an item from your inventory. These items cannot be recovered! It''s much cleaner than just dropping the item in the nearest room.</dd>
	<dt><a name="drop">drop &lt;something&gt;</a></dt>
	<dd>this will make your character drop something on the ground. Be careful though. If one of the other persons picks it up and doesn''t give it back to you, there is no way you can get it back. See also <em>search, get, inventory</em>.</dd>
	<dt><a name="retrieve">retrieve &lt;something&gt; from &lt;container&gt;</a></dt>
	<dd>makes your character retrieve one of his/her items from a container. A container is simply another item but with the added possiblity of storing other items. The container in question may be in your inventory or might be lying on the floor in the current room somewhere.
	<p>You should be able to look at the container to see what it contains.</p>

	<p>Be aware that not all items are containers. See also <em>put</em>.</p>
	</dd>
	<dt><a name="put">put &lt;something&gt; in &lt;container&gt;</a></dt>
	<dd>makes your character put one of his/her items into a container. A container is simply another item but with the added possiblity of storing other items. The container in question may be in your inventory or might be lying on the floor in the current room somewhere.
	<p>You should be able to look at the container to see what it contains.</p>

	<p>Be aware that not all items are containers. See also <em>retrieve</em>.</p>
	</dd>
	<dt><a name="give">give &lt;something&gt; to &lt;person&gt;</a></dt>
	<dd>this will make your character give an item to another character. Very nice to pass objects like beer around. See also <em>search, get, inventory</em>.</dd>
	<dt><a name="search">search &lt;something&gt;</a></dt>
	<dd>this will make your character search something. You could try searching the tables in the Inn or the trees or a whole lot of other areas. Sometimes you will find valuable goodies or items that help you to continue in the game. Use it like this : <em>search tree</em>. See also <em>search, get, inventory</em>.</dd>
	<dt><a name="buy">buy &lt;something&gt;</a></dt>
	<dd>this will make your character buy an item at a shop or somewhere else where people can buy things. If you do not have enough money, the computer will say so. If you do, the amount will be automatically withdrawn from your number of coins. See also <em>sell</em>.</dd>
	<dt><a name="sell">sell &lt;something&gt;</a></dt>
	<dd>this will make your character sell something in a shop or another appropriate place. You will get the number of coins that represent the value of the item. See also <em>buy</em>.</dd>
	<dt><a name="light">light &lt;something&gt;</a></dt>
	<dd>this will make your character light something. Up until now this only works with kerolamps. See also <em>extinguish</em>.</dd>
	<dt><a name="extinguish">extinguish &lt;something&gt;</a></dt>
	<dd>this will make your character extinguish something. Up until now this only works with kerolamps. See also <em>light</em>.</dd>
	<dt><a name="wield">wield &lt;something&gt;</a></dt>
	<dd>this will make your character wield something in his left or right hand, depending on who''s free. The item has to be able to be weld however. Use this in combination with weapons like swords, and stuff. See also <em>unwield</em>.</dd>
	<dt><a name="unwield">unwield &lt;something&gt;</a></dt>
	<dd>this will make your character stop wielding <em>something</em> in his left or right hand. The item has to be wielded however. Use this in combination with weapons like swords, and stuff. See also <em>wield</em>.</dd>
	<dt><a name="use">use &lt;something&gt; with &lt;something&gt;</a></dt>
	<dd>this will make your character use an object in your inventory with something you see or have at your disposal. Usefull for doing stuff that isn''t standard.</dd>
	<dt><a name="quit">quit</a></dt>
	<dd>this will make you leave this game. Make sure you use this command, because if you do not you will stay active, you will not be able to login anymore and your character will not be saved.</dd>
	<dt><a name="help">help</a></dt>
	<dd>will show this help-file onto your screen</dd>
	<dt><a name="helphint">help hint</a></dt>
	<dd>will show one line hint as to what you must do in a certain room. Use this very little, because you should be able to solve everything without help.</dd>
	<dt><a name="clear">clear</a></dt>
	<dd>will clear your <em>log</em>, i.e. the part where your hear everything. Handy if that piece is getting really long.</dd>
	<dt><a name="sleep">sleep</a></dt>
	<dd>will make you go asleep. Everybody who talks to you henceforth will get the message that you are sleeping, and that responses are therefore very low. This usually means that the person is currently not present at the terminal. (<em>See</em> <a href="http://www.karchan.org/karchan/help/help.html#awaken">awaken</a>)</dd>
	<dt><a name="awaken">awaken</a></dt>
	<dd>will make you wake up if you are asleep. This means that you are back into the game active. Everybody will see you wake up and will immediately start talking to you.(<em>See</em> <a href="http://www.karchan.org/karchan/help/help.html#sleep">sleep</a>)</dd>
	<dt><a name="who">who</a></dt>
	<dd>will show you who exactly is in the game at the moment.</dd>
	<dt><a name="public">public &lt;message&gt;</a></dt>
	<dd>this will mail a public message onto a messageboard which subsequently can be read by everybody. This only works in rooms that have a messageboard! See also <em>read public</em></dd>
	<dt><a name="readpublic">read public</a></dt>
	<dd>this will read all the public messages and dump them on the screen for you to peruse at your ease.</dd>
	<dt><a name="bigtalk">bigtalk</a></dt>
	<dd>opens a screen in which a very big input-field is present. This will make it possible to you to type in huge amounts of data without having to bother with the problems of a small entry-field. Also are enters available. It can be best used for <em>talking</em> and for <em>mailing</em>.</dd>
	<dt><a name="time">time</a></dt>
	<dd>this will show you the current time on the MUD. It functions as a GMT (Greenwich Mean Time) for all users.</dd>
	<dt><a name="date">date</a></dt>
	<dd>this will show you the current date on the MUD. It functions as a GMT (Greenwich Mean Time) for all users.</dd>
	<dt><a name="agree">&lt;emotion&gt; [to &lt;person&gt;]</a></dt>
	<dd>These emotions will be helpfull in conveying the state of mind you are in to other users. The possible emotions are: agree, apologize, blink, bow, cheer, chuckle, laugh, cough, curtsey, dance, disagree, flinch, flirt, frown, giggle, glare, grimace, grin, groan, growl, grumble, grunt, hmm, howl, hum, kneel, listen, melt, mumble, mutter, nod, purr, shrug, sigh, smile, smirk, snarl, sneeze, stare, think, wave, whistle, wince, wink, wonder</dd>
	<dt><a name="caress">&lt;action&gt; &lt;person&gt;</a></dt>
	<dd>these actions will be helpfull when you want to convey an action to a certain person in the game. Possible actions are: caress, comfort, confuse, congratulate, cuddle, fondle, greet, hug, ignore, kick, kiss, knee, lick, like, love, nudge, pat, pinch, poke, slap, smooch, sniff, squeeze, tackle, thank, tickle, worship</dd>
	<dt>eyebrow</dt>
	<dd>You will raise one eyebrow.</dd>
	<dt><a name="introduce">introduce me</a></dt>
	<dd>this will introduce yourself to everybody in the room. They will all see what''s your name, your title and the way you look.</dd>
	<dt><a name="title">title &lt;title name&gt;</a></dt>
	<dd>this will change your title. I have had a lot of different requests for changing people''s title. Now with this command they can do it themselves.</dd>
	<dt><a name="pkill">pkill &lt;on,off&gt;</a></dt>
	<dd>this will change your character from a character that can be attacked or killed by other players to a character that cannot be killed and vice versa. You get a message on your screen saying what the status of <em>pkill</em> is. Pkill is standardly set to "off".</dd>
	<dt><a name="guild">guild &lt;message&gt;</a></dt>
	<dd>this will display a message to all guildmembers that are currently online. Basically, a simple chat-channel.</dd>
	<dt><a name="guildapply">guildapply &lt;guildname&gt;</a></dt>
	<dd>this will make you and your character apply for membership of a certain guild. The guildmaster must either <a href="http://www.karchan.org/karchan/help/help.html#guildaccept">guildaccept</a> or <a href="http://www.karchan.org/karchan/help/help.html#guildreject">guildreject</a> your application. You can apply for many guilds, but only the last one will be registered.</dd>
	<dt><a name="guilddetails">guilddetails</a></dt>
	<dd>this will show you all the information on the guild you are currently a member of. Among the information provided are the current members and the current people who wish to join the guild.</dd>
	<dt><a name="guildaccept">guildaccept &lt;person&gt;</a></dt>
	<dd>this command, executed by the guildmaster, will make a person who has expressed the desire to join a guild a member of the guild. This will only work if the person has expressed the wish to join using the <a href="http://www.karchan.org/karchan/help/help.html#guildapply">guildapply</a> command. This will only work if both the guildmaster and the interested party are playing the game.</dd>
	<dt><a name="guildreject">guildreject &lt;person&gt;</a></dt>
	<dd>this command, executed by the guildmaster, will make a person who has expressed the desire to join a guild <em>NOT</em> a member of the guild. This will only work if the person has expressed the wish to join using the <a href="http://www.karchan.org/karchan/help/help.html#guildapply">guildapply</a> command. This will only work if both the guildmaster and the interested party are playing the game.</dd>
	<dt><a name="guildremove">guildremove &lt;person&gt;</a></dt>
	<dd>this command, executed by the guildmaster, will remove a guildmember from a guild.</dd>
	<dt><a name="guilddescription">guilddescription &lt;description&gt;</a></dt>
	<dd>this command, executed by the guildmaster, will set the description of the guild. The description can be a very long text.</dd>
	<dt><a name="guildtitle">guildtitle &lt;title&gt;</a></dt>
	<dd>this command, executed by the guildmaster, will set the title of the guild. The difference between the title of a guild and the name of a guild, is that the <em>name</em> can readily be used in commands where the title is used in displaying information to the player.</dd>
	<dt><a name="guildurl">guildurl &lt;url&gt;</a></dt>
	<dd>this command, executed by the guildmaster, will set the homepage of the guild. An valid URL should be entered including the <em>http://</em>.</dd>
	<dt><a name="guildaddrank">guildaddrank &lt;rankid&gt; &lt;ranktitle&gt;</a></dt>
	<dd>this command, executed by the guildmaster, will add a rank to the guild. See <a href="http://www.karchan.org/karchan/help/help.html#guilddelrank">guilddelrank</a> and <a href="http://www.karchan.org/karchan/help/help.html#guildassign">guildassign</a>.</dd>
	<dt><a name="guilddelrank">guilddelrank &lt;rankid&gt;</a></dt>
	<dd>this command, executed by the guildmaster, will remove a rank from the guild. See <a href="http://www.karchan.org/karchan/help/help.html#guildaddrank">guildaddrank</a> and <a href="http://www.karchan.org/karchan/help/help.html#guildassign">guildassign</a>.</dd>
	<dt><a name="guildassign">guildassign &lt;rankid&gt; &lt;name&gt;</a></dt>
	<dd>this command, executed by the guildmaster, will promote a guildmember to a rank. See <a href="http://www.karchan.org/karchan/help/help.html#guilddelrank">guilddelrank</a> and <a href="http://www.karchan.org/karchan/help/help.html#guildaddrank">guildaddrank</a>.</dd>
	<dt><a name="fullyignore">fully ignore &lt;name&gt;</a></dt>
	<dd>this command, will make you ignore all output of a certain player. See <a href="http://www.karchan.org/karchan/help/help.html#acknowledge">acknowledge</a>.</dd>
	<dt><a name="acknowledge">acknowledge &lt;name&gt;</a></dt>
	<dd>this command, will make you acknowledge all output of a certain player. This is used to negate the <em>ignore</em> effect. See <a href="http://www.karchan.org/karchan/help/help.html#fullyignore">fully ignore</a>.</dd>
</dl>

<p><a name="appC"></a></p>

<h2><a href="http://www.karchan.org/karchan/help/help.html#TABLE">Appendix C. Index of all commands</a></h2>

<p>&nbsp;</p>

<table>
	<tbody>
		<tr>
			<td>
			<div><span style="font-size:
small;">A</span>

			<p><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#acknowledge">acknowledge</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">agree</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">apologize</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#ask">ask</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#askto">ask to</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#awaken">awaken</a></span></span><br />
			&nbsp;</p>

			<div><span style="font-size:
small;"><span><span style="font-size:
small;">B</span></span></span>

			<p><span style="font-size:
small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#agree">blink</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">bow</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#buy">buy</a></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;">C</span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#caress">caress</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">cheer</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">chuckle</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#clear">clear</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#close">close</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">comfort</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">confuse</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">congratulate</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">cough</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">cuddle</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">curtsey</a></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size:
small;">D</span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#agree">dance</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#date">date</a><br />
			<a href="#destroy">destroy</a><br />
			<a href="#agree">disagree</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#drop">drop</a></span></span></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;">E</span></span></span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#extinguish">extinguish</a></span></span></span></span></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size:
small;">F</span></span></span></span></span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#fight">fight</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">flinch</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">flirt</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">fondle</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">frown</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#fullyignore">fully ignore</a></span></span></span></span></span></span></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size:
small;">G</span></span></span></span></span></span></span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#get">get</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">giggle</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">glare</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#goeast">go east</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#gonorth">go north</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#gosouth">go south</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#gowest">go west</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">greet</a></span></span></span></span></span></span></span></span></span></span></span></span></span></span></p>
			</div>
			</div>
			</div>
			</div>
			</div>
			</div>
			</div>
			</td>
			<td><a href="http://www.karchan.org/karchan/help/help.html#agree">grimace</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">grin</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">groan</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">growl</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">grumble</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">grunt</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guild">guild</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guildaccept">guildaccept</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guildaddrank">guildaddrank</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guildapply">guildapply</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guildassign">guildassign</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guilddelrank">guilddelrank</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guilddescription">guilddescription</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guilddetails">guilddetails</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guildreject">guildreject</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guildremove">guildremove</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guildtitle">guildtitle</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#guildurl">guildurl</a><br />
			&nbsp;
			<div><span style="font-size: small;">H</span>
			<p><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#help">help</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#helphint">help hint</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">hmm</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">howl</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">hug</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">hum</a></span></span><br />
			&nbsp;</p>

			<div><span style="font-size:
small;"><span><span style="font-size:
small;">I</span></span></span>

			<p><span style="font-size:
small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#caress">ignore</a> See also <a href="http://www.karchan.org/karchan/help/help.html#fullyignore">fully ignore</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#introduce">introduce me</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#inventory">inventory</a></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;">K</span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#caress">kick</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#kill">kill</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">kiss</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">knee</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">kneel</a></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size:
small;">L</span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#laugh">laugh</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">lick</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#light">light</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">like</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">listen</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#look%20at">look at</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">love</a></span></span></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;">M</span></span></span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#agree">melt</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">mumble</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">mutter</a></span></span></span></span></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size:
small;">N</span></span></span></span></span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#agree">nod</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">nudge</a></span></span></span></span></span></span></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size:
small;">O</span></span></span></span></span></span></span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#open">open</a></span></span></span></span></span></span></span></span></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;">P</span></span></span></span></span></span></span></span></span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#caress">pat</a></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></p>
			</div>
			</div>
			</div>
			</div>
			</div>
			</div>
			</div>
			</div>
			</td>
			<td><a href="http://www.karchan.org/karchan/help/help.html#caress">pinch</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#pkill">pkill</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">poke</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#public">public</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">purr</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#put">put</a><br />
			&nbsp;
			<div><span style="font-size: small;">Q</span>
			<p><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#quit">quit</a></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size: small;">R</span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#read">read</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#readpublic">read public</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#retrieve">retrieve</a></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;">S</span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#say">say</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#sayto">say to</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#search">search</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#sell">sell</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#shout">shout</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#shoutto">shout to</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">shrug</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">sigh</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">slap</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#sleep">sleep</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">smile</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">smirk</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">smooch</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">snarl</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">sneeze</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">sniff</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">squeeze</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">stare</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#stopfighting">stop fighting</a></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size:
small;">T</span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#caress">tackle</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#bigtalk">talk, big</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#tellto">tell to</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">thank</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">think</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">tickle</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#time">time</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#title">title</a></span></span></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;">U</span></span></span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#unwield">unwield</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#use">use</a></span></span></span></span></span></span></span></span></span></span><br />
			&nbsp;</p>

			<div><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size:
small;">W</span></span></span></span></span></span></span></span></span></span></span>

			<p><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><span style="font-size: small;"><span><span style="font-size:
small;"><span><span style="font-size: small;"><span><a href="http://www.karchan.org/karchan/help/help.html#agree">wave</a></span></span></span></span></span></span></span></span></span></span></span></span></p>
			</div>
			</div>
			</div>
			</div>
			</div>
			</div>
			</td>
			<td><span><a href="http://www.karchan.org/karchan/help/help.html#whimpy">whimpy</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#whisperto">whisper to</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">whistle</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#who">who</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#wield">wield</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">wince</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">wink</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#agree">wonder</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#caress">worship</a><br />
			<a href="http://www.karchan.org/karchan/help/help.html#whisper">whisper</a></span></td>
		</tr>
	</tbody>
</table>

<p><a name="appD"></a></p>

<h2><a href="http://www.karchan.org/karchan/help/help.html#TABLE">Appendix D. List of all Adverbs </a></h2>

<p>(Sorry for the formatting, if anyone has a better idea and the time, go ahead.)</p>

<p>absentmindedly aimlessly amazedly amusedly angrily anxiously appreciatively appropriately archly astonishingly attentively badly barely belatedly bitterly boringly breathlessly briefly brightly brotherly busily carefully cautiously charmingly cheerfully childishly clumsily coaxingly coldly completely confidently confusedly contentedly coquetishly courageously coyly crazily cunningly curiously cutely cynically dangerously deeply defiantly dejectedly delightedly delightfully deliriously demonically depressively derisively desperately devilishly dirtily disappointedly discretely disgustedly doubtfully dreamily dubiously earnestly egocentrically egoistically encouragingly endearingly enthusiastically enviously erotically evilly exhaustedly exuberantly faintly fanatically fatherly fiercefully firmly foolishly formally frantically friendly frostily funnily furiously generously gleefully gracefully graciously gratefully greedily grimly happily harmonically headlessly heartbrokenly heavily helpfully helplessly honestly hopefully humbly hungrily hysterically ignorantly impatiently inanely indecently indifferently innocently inquiringly inquisitively insanely instantly intensely interestedly ironically jauntily jealously joyfully joyously kindly knowingly lazily loudly lovingly lustfully madly maniacally melancholically menacingly mercilessly merrily mischieviously motherly musically mysteriously nastily naughtily nervously nicely noisily nonchalantly outrageously overwhelmingly painfully passionately patiently patronizingly perfectly personally physically pitifully playfully politely professionally profoundly profusely proudly questioningly quickly quietly quizzically randomly rapidly really rebelliously relieved reluctantly remorsefully repeatedly resignedly respectfully romantically rudely sadistically sadly sarcastically sardonically satanically scornfully searchingly secretively seductively sensually seriously sexily shamelessly sheepishly shyly sickly significantly silently sisterly skilfully sleepily slightly slowly slyly smilingly smugly socially softly solemnly strangely stupidly sweetly tearfully tenderly terribly thankfully theoretically thoughtfully tightly tiredly totally tragically truly trustfully uncontrollably understandingly unexpectedly unhappily unintentionally unknowingly vaguely viciously vigorously violently virtually warmly wearily wholeheartedly wickedly wildly wisely wistfully</p>

<p><a name="appE"></a></p>

<h2><a href="http://www.karchan.org/karchan/help/help.html#TABLE">Appendix E. Copyrights, Trademarks and Credits</a></h2>

<p>The following things in this game have a copyright. If any company that finds some of its material on this HomePage objects to it being there, let them contact me and I will remove the afore mentioned item.</p>

<p>Text just outside the cave<br />
Book: The Southwest<br />
Author: Elliot Porter</p>

<p>the game Kyrandia</p>

<p>Land of Karchan</p>

<p>Coca Cola<br />
From the Coca-Cola Company</p>

<p>Sprite<br />
From the Coca-Cola Company</p>

<p>Tea - picture<br />
http://branch.com/teas/gifts/cup.gif</p>

<p>Whisky - Picture<br />
Ben Nevis Distillery - Fort William, Scotland - Distillery bottled deluxe blended and rare single malt Scotch whisky. Fort William, Scotland</p>

<p>Vodka - Picture<br />
Finlandia Fantasies</p>

<p>Front of Church<br />
The oldest stone church in the Walled City dates back to 1587. An intricately carved door opens to the church. Of great interest are the baroque pulpit, trompel''oeil paintings,molave choir stalls and an 18th century pipe organ. The monastery houses impressive collection of religious and secular art.<br />
http://www.lc.tut.ac.jp/cheek/phil2a.gif</p>

<p>Ale - Picture<br />
Bass Ale<br />
http://www.guinnessimportco.com/bassale/cask.jpg</p>

<p>Breads picture<br />
Savage''s Bakery and Restaurant<br />
http://www.homewood.net/savages/</p>

<p>Credits</p>

<p>The cgic-library used to interpret the Common Gateway Interface<br />
cgic, copyright 1996 by Thomas Boutell.</p>

<p><a name="appF"></a></p>

<h2><a href="http://www.karchan.org/karchan/help/help.html#TABLE">Appendix F. Thanks go to the following people</a></h2>

<ol>
	<li>Joe Petty<br />
	petty@kids.wustil.edu<br />
	manatoba<br />
	For making this mud a safe place</li>
	<li>James Edge<br />
	scout@albany.net<br />
	Kastel<br />
	For finding me the images, I need.</li>
	<li>Maarten Kuipers<br />
	mb509@hi.ft.hse.nl<br />
	<br />
	For also finding me the images, I need.</li>
	<li>Edwin Mons<br />
	edwinm@il.fontys.nl<br />
	Intosi<br />
	For giving me the programming advice I need</li>
	<li>Joris van Liempd<br />
	<br />
	Jiros<br />
	For taking an interest in my exertions.</li>
	<li>and anybody else I might have forgotten (Sorry)</li>
</ol>

</div>');
replace into templates 
(id, name, created, modified, content)
values(19, "help/tech_specs", now(), now(), '<div class="container">
<p style="text-align: center;"><img alt="" src="http://www.karchan.org/images/jpeg/frame.jpg" style="display: block; margin-left:
auto; margin-right: auto;" /><br />
<em>Picture of my Server in full Multi-user Action</em></p>

<p>The information on this page is terribly old. Some years ago, when I was still a student and I was a member of the InternetUsersAssociation Interlink, we got a webcam and I ... convinced my fellow students to make a picture of my computer, the computer I had then. Here''s the original text as it appeared on the website:</p>

<p><em>First of all some information about the environment in which this is all running. The computer that is running this here game is my own property and I have situated this computer at my school, where they have Internet. I''ve done this through my favorite InternetUsersClub InterLink, who operate at my school, and are providing housing for my computer. However, when I graduate (next year) this computer is going to have to go. Anyone with a solution is welcome to mail it to me.</em></p>

<p><em>Here follow some information about what is running:</em></p>

<p><em>Machine specs:</em></p>

<ul>
	<li><em>PC Pentium 133 </em></li>
	<li><em>Silverline Bigtower (200W) </em></li>
	<li><em>Motherboard ASUS P55T2P4 </em></li>
	<li><em>CPU Pentium cooler </em></li>
	<li><em>SIMM 16MB 72-pins (4MBx32) EDO (2x) </em></li>
	<li><em>Diskdrive 1,44Mb </em></li>
	<li><em>HDD 3,2GB SCSI-2 Fireball </em></li>
	<li><em>24 Speed USDRIVE SCSI CDROM </em></li>
	<li><em>S3Virge 4 MB PCI </em></li>
	<li><em>Monitor 14" Philips 104SLE </em></li>
	<li><em>Windows Keyboard Mitsumi </em></li>
	<li><em>Mouse Logitec Pilot - serial </em></li>
	<li><em>NCR Fast SCSI-2 controller </em></li>
	<li><em>Networkcard NE2000 Generic Prolan Combo PCI (2x) </em></li>
	<li><em>Smart Back-UPS Pro 420 VA </em></li>
</ul>

<p>This is now hopelessly outdated, but I never felt like upgrading the page. It is pure nostalgia to me.</p>

</div>');
replace into templates 
(id, name, created, modified, content)
values(20, "help/source", now(), now(), '<div class="container">
<h2>Introduction</h2>

<p>This page contains most of the introductory information necessary for you to get a similar Mud working like "<a href="http://www.karchan.org/">Land of Karchan</a>".</p>

<h2>License</h2>

<p>The license I use for the mud is the <a href="http://www.gnu.org/">GPL</a>. As far as I know I am only using existing GPL or LGPL software to create my own software so this should be no problem.</p>

<p>Why do I give away the source code? Well, it''s part of a new way of doing things. By keeping all source code private in the hands of companies you take away a piece of knowledge of which a lot of people can benefit. Try looking at it like instead of a company holding back the formula to the solution to a disease, having the company give out the formula for public use, thereby helping people all over the world. A lot of my source code is based upon other people''s source code, who basically did the same thing as me, thereby creating a base on which I could build my mud.</p>

<h2>Documentation</h2>

<p>The java documentation ("javadoc") of the backend is available <a href="https://www.karchan.org/karchandocs" target="_blank">here</a>.</p>

<p>The typescript documentation ("typedoc") of the frontend is available&nbsp;<a href="https://www.karchan.org/karchanpersonal/doc" target="_blank">here</a>.</p>

<h2><span style="color: inherit; font-family: inherit;">References</span></h2>

<p>I''ve put online the <em>entire</em> source code and it is easily viewable to <em>anyone</em>. You can find it on <a href="https://github.com" target="_blank" title="Github">Github</a> at <a href="https://github.com/maartenl/Land-of-Karchan" title="Land of Karchan - Sourcecode">this location</a>. It also contains a simple README.md that you can use to get something working.</p>

<h3>Licensing</h3>

<ul>
	<li>GNU - <a href="http://www.gnu.org/">http:/www.gnu.org</a></li>
</ul>

<h2>Disclaimer</h2>

<p>I''m giving you the source code of my mud in order for you to make something original with it. There is a clear distantiation between my mud (<em>Maarten''s MUD</em>) and the <em>Land of Karchan</em>. While <em>Maarten''s Mud</em> does fall under the GPL, <em>Land of Karchan</em> does no such thing and is copyrighted by me and others.</p>

<p>Have fun experimenting, and do send me an <a href="mailto:maarten_l@yahoo.com">email</a> if something is not clear.</p>

<div><em>Karn (Ruler of Karchan,<br />
Keeper of the Key to the Room of Lost Souls)</em></div>

</div>');
replace into templates 
(id, name, created, modified, content)
values(21, "help/security", now(), now(), '<div class="container">
<p>The guidelines below are available for <i>you</i> to follow, for your own safety.</p>

<p>These guidelines are by no means specific to Karchan, but can be a great help with other websites as well.</p>

<ol>
	<li><strong><span style="font-size: large;">Keep your Operating System up to date.</span></strong></li>
	<li><strong><span style="font-size: large;">Keep your Web Browser up to date.</span></strong></li>
	<li><strong><span style="font-size: large;">Be wary of downloading plugins into your Web Browser.</span></strong></li>
	<li><strong><span style="font-size: large;">Use the SSL enabled website at <a href="https://www.karchan.org">https://www.karchan.org</a></span></strong></li>
	<li><strong><span style="font-size: large;">Do not use a password for Land of Karchan that you also use for other (more important) websites.</span></strong></li>
	<li><strong><span style="font-size: large;">Most Web Browsers have a Privacy Mode, that might be handy when accessing Karchan from a public place.</span></strong></li>
	<li><strong><span style="font-size: large;">When you have doubts about Karchan, always contact a deputy regarding your worries. It might be nothing, but still...</span></strong></li>
</ol>
</div>');
replace into templates 
(id, name, created, modified, content)
values(22, "help/faq", now(), now(), '<div class="container">
<div class="accordion" id="accordionFaq">
<#list faq as faq>
  <div class="card">
    <div class="card-header" id="heading${faq.id}">
      <h2 class="mb-0">
        <button class="btn btn-link" type="button" data-toggle="collapse" data-target="#collapse${faq.id}" aria-expanded="true" aria-controls="collapse${faq.id}">
          ${faq.question}
        </button>
      </h2>
    </div>

    <div id="collapse${faq.id}" class="collapse show" aria-labelledby="heading${faq.id}" data-parent="#accordionFaq">
      <div class="card-body">
        ${faq.answer}
      </div>
    </div>
  </div>
</#list>
</div>
</div>');
replace into templates 
(id, name, created, modified, content)
values(23, "new_character", now(), now(), '
<div class="container">
  <p>You are presently in the room of lost souls. It is dark all around you, but you see transparent souls everywhere. You are yourself a soul. A sign is visible on the west-wall. You make an attempt to read it. After a while you figure it out. This is what it says:</p>

  <p><em>Hello</em></p>

  <p><em>Let me introduce myself. I am Karn. You probably haven''t heard of me, but I am the diety of this place, and I am here to see that everything goes according to the rules that I have made.</em></p>

  <p><em>You are just a spirit, a soul, a ghost, a what-ever-you-call-it. You have no substance whatsoever. You can''t go about playing with no body! So here you can mould and form your own body, just the way <strong>you</strong> like it. </em></p>

  <p><em>There are certain rules which you have to abide by if you want to be able to play without any problems: </em></p>

  <ul>
    <li><em>leave this game every time you play it with the commando <strong>quit</strong>. If you don''t, your session will continue without you. </em></li>
    <li><em>do not start another session while you are still playing in one, that will result in an error </em></li>
    <li><em>read the book in the cave </em></li>
    <li><em>do not go to another link while you are playing, this will make it extremely hard to come back! </em></li>
    <li><em>remember that this game is in the development stages yet, al lot of things won''t work, but this game will keep getting better </em></li>
  </ul>

  <p><em>That is about it. More detailed information can be found in the book mentioned. If you have entered everything, please hit the <strong>Submit</strong> button.</em></p>

  <p style="text-align: right;"><em>Karn, Ruler of Karchan, Keeper of the Key to the Room of Lost Souls </em></p>

  <script src="/javascripts/newchar.js" type="text/javascript"></script>
  <form accept-charset="UTF-8" enctype="multipart/form-data" id="webform-client-form-18" method="post" onsubmit="createNewchar();return false;">
    <div class="form-group">
      <label for="edit-submitted-fictional-name">Name <span class="form-required">*</span></label>
      <input type="text" class="form-control" id="edit-submitted-fictional-name" aria-describedby="fictionalNameHelp" size="19" maxlength="19">
      <small id="fictionalNameHelp" class="form-text text-muted">The fictional name of your new character.</small>
    </div>
    <div class="form-group">
      <label for="edit-submitted-password">Password <span class="form-required">*</span></label>
      <input type="password" class="form-control" id="edit-submitted-password" placeholder="Password" size="60" maxlength="128">
    </div>
    <div class="form-group">
      <label for="edit-submitted-password2">Password (again for verification) <span class="form-required">*</span></label>
      <input type="password" class="form-control" id="edit-submitted-password2" placeholder="Password again" size="60" maxlength="128">
    </div>
    <div class="form-group">
      <label for="edit-submitted-title-except-name">Title</label>
      <input type="text" class="form-control" id="edit-submitted-title-except-name" size="50" maxlength="79">
    </div>
    <div class="form-group">
      <label for="edit-submitted-real-name">Your name</label>
      <input type="text" class="form-control" id="edit-submitted-real-name" size="49" maxlength="49">
    </div>
    <div class="form-group">
      <label for="edit-submitted-email">Email address</label>
      <input type="email" class="form-control" id="edit-submitted-email" aria-describedby="emailHelp" placeholder="Enter email">
      <small id="emailHelp" class="form-text text-muted">This will only be used for sending you password reset links.</small>
    </div>
    <div class="form-group">
      <label for="edit-submitted-race">Race <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-race">
        <option selected="selected" value="human">human</option>
        <option value="dwarf">dwarf</option>
        <option value="elf">elf</option> 
      </select>
    </div>

    <div class="form-group">
      <label>Sex <span class="form-required">*</span></label>
    </div>
    <div class="form-check">
      <input class="form-check-input" type="radio" name="sexRadios" id="edit-submitted-sex-1" value="male" checked>
      <label class="form-check-label" for="edit-submitted-sex-1">
        male
      </label>
    </div>
    <div class="form-check">
      <input class="form-check-input" type="radio" name="sexRadios" id="edit-submitted-sex-2" value="female">
      <label class="form-check-label" for="edit-submitted-sex-2">
        female
      </label>
    </div>

    <div class="form-group">
      <label for="edit-submitted-age">Age <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-age">
        <option selected="selected" value="young">young</option>
        <option value="middle-aged">middle-aged</option>
        <option value="old">old</option> 
        <option value="very old">very old</option> 
      </select>
    </div>

    <div class="form-group">
      <label for="edit-submitted-length">Length <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-length">
        <option value="very small">very small</option>
        <option value="small">small</option>
        <option value="medium height">medium height</option> 
        <option selected="selected" value="tall">tall</option> 
        <option value="very tall">very tall</option> 
      </select>
    </div>

    <div class="form-group">
      <label for="edit-submitted-width">Width <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-width">
        <option value="very thin">very thin</option>
        <option value="thin">thin</option>
        <option value="transparent">transparent</option>
        <option value="skinny">skinny</option>
        <option value="slender">slender</option>
        <option value="medium">medium</option>
        <option value="corpulescent">corpulescent</option>
        <option value="fat">fat</option>
        <option value="very fat">very fat</option>
        <option value="ascetic">ascetic</option>
        <option value="athlethic">athlethic</option> 
      </select>
    </div>

    <div class="form-group">
      <label for="edit-submitted-complexion">Complexion <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-complexion">
        <option value="black">black</option>
        <option value="brown-skinned">brown-skinned</option>
        <option value="dark-skinned">dark-skinned</option>
        <option value="ebony-skinned">ebony-skinned</option>
        <option value="green-skinned">green-skinned</option>
        <option value="ivory-skinned">ivory-skinned</option>
        <option value="light-skinned">light-skinned</option>
        <option value="pale">pale</option>
        <option value="pallid">pallid</option>
        <option selected="selected" value="swarthy">swarthy</option>
        <option value="white">white</option>
        <option value="none">none</option> 
      </select>
    </div>

    <div class="form-group">
      <label for="edit-submitted-eyes">Eyes <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-eyes">
        <option selected="selected" value="black-eyed">black-eyed</option>
        <option value="blue-eyed">blue-eyed</option>
        <option value="brown-eyed">brown-eyed</option>
        <option value="dark-eyed">dark-eyed</option>
        <option value="gray-eyed">gray-eyed</option>
        <option value="green-eyed">green-eyed</option>
        <option value="one-eyed">one-eyed</option>
        <option value="red-eyed">red-eyed</option>
        <option value="round-eyed">round-eyed</option>
        <option value="slant-eyed">slant-eyed</option>
        <option value="squinty-eyed">squinty-eyed</option>
        <option value="yellow-eyed">yellow-eyed</option>
        <option value="none">none</option> 
      </select>
    </div>

    <div class="form-group">
      <label for="edit-submitted-face">Face <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-face">
        <option value="big-nosed">big-nosed</option>
        <option value="chinless">chinless</option>
        <option value="dimpled">dimpled</option>
        <option value="double-chinned">double-chinned</option>
        <option value="furry-eared">furry-eared</option>
        <option value="hook-nosed">hook-nosed</option>
        <option value="jug-eared">jug-eared</option>
        <option value="knobnosed">knobnosed</option>
        <option selected="selected" value="long-faced">long-faced</option>
        <option value="pointy-eared">pointy-eared</option>
        <option value="poppy-eyed">poppy-eyed</option>
        <option value="potato-nosed">potato-nosed</option>
        <option value="pug-nosed">pug-nosed</option>
        <option value="red-nosed">red-nosed</option>
        <option value="roman-nosed">roman-nosed</option>
        <option value="round-faced">round-faced</option>
        <option value="sad-faced">sad-faced</option>
        <option value="square-faced">square-faced</option>
        <option value="square-jawed">square-jawed</option>
        <option value="stone-faced">stone-faced</option>
        <option value="thin-faced">thin-faced</option>
        <option value="upnosed">upnosed</option>
        <option value="wide-mouthed">wide-mouthed</option>
        <option value="none">none</option> 
      </select>
    </div>

    <div class="form-group">
      <label for="edit-submitted-hair">Hair <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-hair">
        <option value="bald">bald</option>
        <option value="balding">balding</option>
        <option selected="selected" value="black-haired">black-haired</option>
        <option value="blond-haired">blond-haired</option>
        <option value="blue-haired">blue-haired</option>
        <option value="brown-haired">brown-haired</option>
        <option value="chestnut-haired">chestnut-haired</option>
        <option value="dark-haired">dark-haired</option>
        <option value="gray-haired">gray-haired</option>
        <option value="green-haired">green-haired</option>
        <option value="light-haired">light-haired</option>
        <option value="long haired">long haired</option>
        <option value="orange-haired">orange-haired</option>
        <option value="purple-haired">purple-haired</option>
        <option value="white-haired">white-haired</option>
        <option value="none">none</option> 
      </select>
    </div>

    <div class="form-group">
      <label for="edit-submitted-beard">Beard <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-beard">
        <option value="black-bearded">black-bearded</option>
        <option value="blond-bearded">blond-bearded</option>
        <option value="blue-bearded">blue-bearded</option>
        <option value="brown-bearded">brown-bearded</option>
        <option value="clean">clean</option>
        <option value="shaven">shaven</option>
        <option value="fork-bearded">fork-bearded</option>
        <option value="gray-bearded">gray-bearded</option>
        <option value="green-bearded">green-bearded</option>
        <option value="long bearded">long bearded</option>
        <option value="mustachioed">mustachioed</option>
        <option value="orange-bearded">orange-bearded</option>
        <option value="purple-bearded">purple-bearded</option>
        <option value="red-bearded">red-bearded</option>
        <option value="thinly-bearded">thinly-bearded</option>
        <option value="white-bearded">white-bearded</option>
        <option value="with a ponytale">with a ponytale</option>
        <option selected="selected" value="none">none</option> 
      </select>
    </div>

    <div class="form-group">
      <label for="edit-submitted-arms">Arms <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-arms">
        <option value="long-armed">long-armed</option>
        <option value="short-armed">short-armed</option>
        <option value="thick-armed">thick-armed</option>
        <option value="thin-armed">thin-armed</option>
        <option selected="selected" value="none">none</option> 
      </select>
    </div>

    <div class="form-group">
      <label for="edit-submitted-legs">Legs <span class="form-required">*</span></label>
      <select class="form-control" id="edit-submitted-legs">
        <option value="bandy-legged">bandy-legged</option>
        <option value="long-legged">long-legged</option>
        <option value="short-legged">short-legged</option>
        <option value="skinny-legged">skinny-legged</option>
        <option value="thin-legged">thin-legged</option>
        <option value="thick-legged">thick-legged</option>
        <option selected="selected" value="none">none</option> 
      </select>
    </div>

    <button type="submit" id="edit-submit" class="btn btn-primary">Submit</button>
  </form>
</div>');

replace into templates 
(id, name, created, modified, content)
values(24, "chronicles/person", now(), now(), '
<div class="container">
  <script src="/javascripts/charactersheet.js" type="text/javascript"></script>
  <h1 id="page-title">Charactersheet of</h1>

  <p><a href="/chronicles/people.html">Back to People</a></p>

  <div id="karchan_charactersheet">Loading content. Please hold...</div>
</div>');

-- Game --
replace into templates 
(id, name, created, modified, content)
values(25, "game/settings", now(), now(), '
<div class="container-fluid">
  <iframe id="embeddedIframe" src="/karchanpersonal" style="position:absolute;" height="100%" class="autosizeiframe-monitored-height" width="100%" frameborder="0"></iframe>
</div>');

replace into templates 
(id, name, created, modified, content)
values(26, "game/play", now(), now(), '
<div class="container-fluid">
  <p><img alt="compass" border="0" src="/images/png/compass.png" usemap="#roosmap" /><map name="roosmap"><area alt="North" coords="0,0,80,75,160,0,0,0" href="javascript:void(0)" onclick="goNorth();" shape="poly" /> <area alt="South" coords="0,151,80,75,160,151,0,151" href="javascript:void(0)" onclick="goSouth();" shape="poly" /> <area alt="West" coords="0,0,80,75,0,151,0,0" href="javascript:void(0)" onclick="goWest();" shape="poly" /> <area alt="East" coords="160,0,80,75,160,151,160,0" href="javascript:void(0)" onclick="goEast()" shape="poly" /></map></p>

  <p><a class="karchanbutton" href="/game/settings.html" target="_blank" title="Read your mail"><span>Mail</span></a></p>

  <p><a class="karchanbutton" href="/game/settings.html" target="_blank" title="Charactersheet"><span>Settings</span></a></p>

  <p><a class="karchanbutton" href="javascript:void(0)" onclick="toggleSleep();return false;"><span id="sleepButtonSpanId">Sleep</span></a></p>

  <p><a class="karchanbutton" href="javascript:void(0)" onclick="toggleEntry();return false;" title="Provide a big entry form"><span>Big talk</span></a></p>

  <p><a class="karchanbutton" href="javascript:void(0)" onclick="clearLog();return false;" title="Clear your log"><span>Clear</span></a></p>

  <p><a class="karchanbutton" href="/game/settings.html" onclick="return quit();" title="Quit the game"><span>Quit</span></a></p>
</div>
<div class="container-fluid">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/js-cookie/2.0.3/js.cookie.min.js">
  </script><script src="/javascripts/play.js" type="text/javascript"></script>
  <div id="page-title">&nbsp;</div>

  <div id="karchan_body">Loading content. Please hold...</div>

  <div id="karchan_log">Loading content. Please hold...</div>

  <p>&nbsp;</p>

  <form id="commandForm" method="post" onsubmit="play(); return false;">
  <input id="command" name="command" size="60" type="text" />
  <textarea class="form-textarea" cols="60" id="bigcommand" name="bigcommand" rows="5" style="display:none"></textarea> 

  <input type="submit" value="Submit" />&nbsp;</form>

  <div id="warning" style="color: red;">&nbsp;</div>
</div>');

replace into templates 
(id, name, created, modified, content)
values(27, "game/goodbye", now(), now(), '
<div class="container">
  <p><img alt="Y" src="/images/gif/letters/y.gif" style="float: left;" />our game has been saved and we look forward to seeing you again in the near future.</p>

  <p>Click above to go somewhere else.</p>
</div>');

replace into templates 
(id, name, created, modified, content)
values(28, "wiki/index", now(), now(), '
<div class="container">
  <#noparse>${wikicontent}</#noparse>
</div>');

replace into templates 
(id, name, created, modified, content)
values(29, "blogs/index", now(), now(), '
<div class="container">
  <nav aria-label="Page navigation example">
    <ul class="pagination">
      <li class="page-item <#if page lte 1>disabled</#if>"><a class="page-link" href="/blogs/index.html?page=${page-1}">Previous</a></li>
  <#list 1..size as i>
      <li class="page-item <#if page == i>active</#if>"><a class="page-link" href="/blogs/index.html?page=${i}">${i}</a></li>
  </#list>
      <li class="page-item <#if page gte size>disabled</#if>"><a class="page-link" href="/blogs/index.html?page=${page+1}">Next</a></li>
    </ul>
  </nav>
<#list blogs as blog> 
  <div class="card m-1">
      <div class="card-body">
          <h5 class="card-title">  
              <a href="/blogs/${blog.urlTitle}.html">${blog.title}</a>
          </h5>
          <h6 class="card-subtitle mb-2 text-muted">Published Date ${blog.createDate?datetime}</h6>
          <p class="card-text">
              ${blog.content?keep_before("</p>")}...
          </p>
          <small class="text-muted">By ${blog.name}.</small>
      </div>
  </div>
</#list>                           
</div>');

replace into templates 
(id, name, created, modified, content)
values(30, "blogs/specific", now(), now(), '
<div class="container">
<#if blog??>
  <div class="card m-1">
      <div class="card-body">
          <h5 class="card-title">  
              ${blog.title}
          </h5>
          <h6 class="card-subtitle mb-2 text-muted">Published Date ${blog.createDate?datetime}</h6>
          <p class="card-text">
              ${blog.content}
          </p>
          <small class="text-muted">By ${blog.name}.</small>
      </div>
  </div>
<#else>
  <div class="alert alert-danger" role="alert">
    <span class="alert-link">Blog</span> not found!
  </div>
  <p>Click <a href="/blogs/index.html">here</a> to return to the blogs.</p>
</#if>
</div>');