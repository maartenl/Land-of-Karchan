#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

update methods
set src = "sayeveryone(""%me says [to Kroonz] :  Can you open an account for me?<BR>"")
if sql("" select 1 from tmp_itemtable where id=36 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
	say(""Kroonz says [to you] : You already have an account here.<BR>"")
	sayeveryone(""Kroonz says [to %me] :  You already have an account here.<BR>"")
	showstandard
else
	say(""Kroonz says [to you] : You now have an account.<BR>"")
	sayeveryone(""Kroonz says [to %me] : You now have an account.<BR>"")
	sql(""insert into tmp_itemtable values(36, '%me', '', 0, -1, '','')"")
	sql(""insert into tmp_itemtable values(37, '%me', '', 0, -1, '','')"")
	sql(""insert into tmp_itemtable values(38, '%me', '', 0, -1, '','')"")
	showstandard
end
return
" where id = 68;

update methods
set src = "sayeveryone(""%me says [to Kroonz] :  Can you close my account?<BR>"")
if sql("" select 1 from tmp_itemtable where id=36 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
	getstring(""select concat(c.amount,"" gold coins, "", b.amount, "" \\\\
		silver coins and "", a.amount, "" copper coins."") \\\\
		from tmp_itemtable a, tmp_itemtable b, tmp_itemtable c \\\\
		where a.id=36 and b.id=37 and c.id=38 \\\\
		and a.search='%me' and b.search='%me' and c.search='%me' and a.belongsto='' \\\\
		and b.belongsto='' and c.belongsto='' and a.room=-1 and b.room=-1 and \\\\
		c.room=-1 and a.wearing='' and b.wearing='' and c.wearing='' and \\\\
		a.wielding='' and b.wielding='' and c.wielding=''"")
	say(""Kroonz closes your account and gives you %string<BR>"")
	sayeveryone(""Kroonz closes %me's account and gives %me %string<BR>"")
	getstring(""select concat(""set gold=gold+"", a.amount,"", \\\\
		silver=silver+"",  b.amount, "", copper=copper+"", c.amount) \\\\
		from tmp_itemtable a, tmp_itemtable b, tmp_itemtable c \\\\
		where a.id=36 and b.id=37 and c.id=38 \\\\
		and a.search='%me' and b.search='%me' and c.search='%me' and a.belongsto='' \\\\
		and b.belongsto='' and c.belongsto='' and a.room=-1 and b.room=-1 and \\\\
		c.room=-1 and a.wearing='' and b.wearing='' and c.wearing='' and \\\\
		a.wielding='' and b.wielding='' and c.wielding=''"")
	sql(""update tmp_usertable %string where name='%me'"")
	sql(""delete from tmp_itemtable where (id=36 or id=37 or id=38) and \\\\
		search='%me' and belongsto='' and room=-1 \\\\
		and wearing='' and wielding=''"")
	showstandard
else
	say(""Kroonz says [to you] : You do not have an account here.<BR>"")
	sayeveryone(""Kroonz says [to %me] :  You do not have an account here.<BR>"")
	showstandard
end
return
" where id = 69;

update methods
set src = "# deposit $ [copper, silver, gold] coin[s]
say(""You say [to Kroonz] :  Can you deposit %02 %03 %04?<BR>"")
sayeveryone(""%me says [to Kroonz] :  Can you deposit %02 %03 %04?<BR>"")
if sql(""select 1 from tmp_itemtable where id=36 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
	if sql(""select 1 from tmp_usertable where '%03'='copper' and name='%me' and copper>=%02 and %02>0"")
		say(""You give Kroonz %02 %03 %04 and he deposits into his safe.<BR>"")		
		sayeveryone(""%me gives Kroonz %02 %03 %04 and he deposits into his safe.<BR>"")		
		sql(""update tmp_usertable set copper=copper-%02 where name='%me'"")
		sql(""update tmp_itemtable set amount=amount+%02 where id=36 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
		showstandard
	end
	if sql(""select 1 from tmp_usertable where '%03'='silver' and name='%me' and silver>=%02 and %02>0"")
		say(""You give Kroonz %02 %03 %04 and he deposits into his safe.<BR>"")		
		sayeveryone(""%me gives Kroonz %02 %03 %04 and he deposits into his safe.<BR>"")		
		sql(""update tmp_usertable set silver=silver-%02 where name='%me'"")
		sql(""update tmp_itemtable set amount=amount+%02 where id=37 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
		showstandard
	end
	if sql(""select 1 from tmp_usertable where '%03'='gold' and name='%me' and gold>=%02 and %02>0"")
		say(""You give Kroonz %02 %03 %04 and he deposits into his safe.<BR>"")		
		sayeveryone(""%me gives Kroonz %02 %03 %04 and he deposits into his safe.<BR>"")		
		sql(""update tmp_usertable set gold=gold-%02 where name='%me'"")
		sql(""update tmp_itemtable set amount=amount+%02 where id=38 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
		showstandard
	end
	say(""Kroonz says [to you] : You do not have that much money...<BR>"")
	sayeveryone(""Kroonz says [to %me] : You do not have that much money...<BR>"")
	showstandard
else
	say(""Kroonz says [to you] : You do not have an account here.<BR>"")
	sayeveryone(""Kroonz says [to %me] :  You do not have an account here.<BR>"")
	showstandard
end
return
" where id = 70;

update methods
set src = "# withdraw $ [copper, silver, gold] coin[s]
say(""You say [to Kroonz] :  I'd like to withdraw %02 %03 %04.<BR>"")
sayeveryone(""%me says [to Kroonz] :  I'd like to withdraw %02 %03 %04.<BR>"")
if sql(""select 1 from tmp_itemtable where id=36 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
	if sql(""select 1 from tmp_itemtable where '%03'='copper' and id=36 and amount>=%02 and search='%me' and belongsto='' and room=-1 and wearing='' and %02>0 and wielding=''"")
		say(""Kroonz gives you %02 %03 %04 from the safe.<BR>"")		
		sayeveryone(""Kroonz gives %me %02 %03 %04 from the safe.<BR>"")		
		sql(""update tmp_usertable set copper=copper+%02 where name='%me'"")
		sql(""update tmp_itemtable set amount=amount-%02 where id=36 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
		showstandard
	end
	if sql(""select 1 from tmp_itemtable where '%03'='silver' and id=37 and amount>=%02 and search='%me' and belongsto='' and room=-1 and wearing='' and %02>0 and wielding=''"")
		say(""Kroonz gives you %02 %03 %04 from the safe.<BR>"")		
		sayeveryone(""Kroonz gives %me %02 %03 %04 from the safe.<BR>"")		
		sql(""update tmp_usertable set silver=silver+%02 where name='%me'"")
		sql(""update tmp_itemtable set amount=amount-%02 where id=37 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
		showstandard
	end
	if sql(""select 1 from tmp_itemtable where '%03'='gold' and id=38 and amount>=%02 and search='%me' and belongsto='' and room=-1 and wearing='' and %02>0 and wielding=''"")
		say(""Kroonz gives you %02 %03 %04 from the safe.<BR>"")		
		sayeveryone(""Kroonz gives %me %02 %03 %04 from the safe.<BR>"")		
		sql(""update tmp_usertable set gold=gold+%02 where name='%me'"")
		sql(""update tmp_itemtable set amount=amount-%02 where id=38 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
		showstandard
	end
	say(""Kroonz says [to you] : I'm sorry but you cannot overdraw your account...<BR>"")
	sayeveryone(""Kroonz says [to %me] : I'm sorry but you cannot overdraw your account...<BR>"")
	showstandard
else
	say(""Kroonz says [to you] : You do not have an account here.<BR>"")
	sayeveryone(""Kroonz says [to %me] :  You do not have an account here.<BR>"")
	showstandard
end
return
" where id = 71;

update methods
set src = "say(""You say [to Kroonz]: I'd like to see my balance.<BR>"")
sayeveryone(""%me says [to Kroonz]: I'd like to see my balance.<BR>"")
# show balance
if sql(""select 1 from tmp_itemtable where id=36 and search='%me' and belongsto='' and room=-1 and wearing='' and wielding=''"")
	say(""Kroonz gets out an old and dusty book out \\\\
		of a huge bookcase to his left. He dumps it on the ledger hiding \\\\
		everyone in a temporary dust cloud. You and him peer intensely over \\\\
		his extremely bad handwriting.<BR>"")
	sayeveryone(""You see Kroonz getting out an old and dusty book out \\\\
		of a huge bookcase to his left. He dumps it on the ledger hiding \\\\
		everyone in a temporary dust cloud. When the dust clears you notice \\\\
		Kroonz and %me peering over some pages.<BR>"")
	getstring(""select concat(c.amount,"" gold coins, "", b.amount, "" \\\\
		silver coins and "", a.amount, "" copper coins."") \\\\
		from tmp_itemtable a, tmp_itemtable b, tmp_itemtable c \\\\
		where a.id=36 and b.id=37 and c.id=38 \\\\
		and a.search='%me' and b.search='%me' and c.search='%me' and a.belongsto='' \\\\
		and b.belongsto='' and c.belongsto='' and a.room=-1 and b.room=-1 and \\\\
		c.room=-1 and a.wearing='' and b.wearing='' and c.wearing='' and \\\\
		a.wielding='' and b.wielding='' and c.wielding=''"")
	say(""In red handwriting you see written ""<FONT COLOR=red><I>%me Owed:   %string</I></FONT>"".<BR>"")
	say(""While you ponder this information, you notice Kroonz rapidly \\\\
		getting the book back between its companions in the bookcase.<BR>"")
	showstandard
else
	say(""Kroonz says [to you] : You do not have an account here.<BR>"")
	sayeveryone(""Kroonz says [to %me] :  You do not have an account here.<BR>"")
	showstandard
end
return
" where id = 72;

update methods
set src = "say(""Nothing happens. I'm afraid you'll have to try something else.<BR>"")
sayeveryone(""%me tries to pull the stone and ... ooh, how exciting... Nothing happens!<BR>"")
showstandard
return
" where id = 73;

update methods
set src = "sayeveryone(""%me tries to push the stone, the stone slides back into \\\\
the wall, and suddenly %me begins to shimmer, and fade away from existence...<BR>"")
sql(""update tmp_usertable set room=364 where name='%me'"")
set room=364
say(""You look around yourself and are surprised to see that your entire surroundings have changed.<BR>"")
sayeveryone(""%me fades into existance.<BR>"")
showstandard
return
" where id = 74;

update methods
set src = "sayeveryone(""%me takes the key from the pillar, yet another immediately appears.<BR>\\\\
When you look around you, %me seems to have suddenly disappeared.<BR>"")
sql(""update tmp_usertable set room=182 where name='%me'"")
set room=182
say(""You get the key from the pillar, and immediately  you appear to be \\\\
back in the sewers. How odd, it just happened, and you didn't notice.<BR>"")
sayeveryone(""%me fades into existance.<BR>"")
showstandard
return
" where id = 75;

update methods
set src = "# learn <skill>
if sql(""select 1 from skills where (name='%02' and %amount=2) or (name=concat('%02',' ','%03') and %amount=3) or (name=concat('%02',' ','%03',' ','%04') and %amount=4)"")
	getstring(""select number from skills where (name='%02' and %amount=2) or (name=concat('%02',' ','%03') and %amount=3) or (name=concat('%02',' ','%03',' ','%04') and %amount=4)"")
	if sql(""select 1 from skilltable where %string=skilltable.number and forwhom='%me'"")
		say(""You have already learned that skill.<BR>"")
		showstandard
	else
		if sql(""select 1 from tmp_usertable where name='%me' and practises=0"")
			say(""You do not have enough practise sessions to learn that skill.<BR>"")
			showstandard
		else
			sql(""insert into skilltable values(%string, '%me',0)"")
			sql(""update tmp_usertable set practises=practises-1 where name='%me'"")
			if sql(""select %amount = 2"")
				sayeveryone(""%me learns %02.<BR>"")
				say(""You learn %02.<BR>"")
			end
			if sql(""select %amount = 3"")
				sayeveryone(""%me learns %02 %03.<BR>"")
				say(""You learn %02 %03.<BR>"")
			end
			if sql(""select %amount = 4"")
				sayeveryone(""%me learns %02 %03 %04.<BR>"")
				say(""You learn %02 %03 %04.<BR>"")
			end
			showstandard
		end
	end
else
	say(""That particular skill is unknown.<BR>"")
	showstandard
end
return
" where id = 76;

update methods
set src = "# practise <skill>
if sql(""select 1 from skills where (name='%02' and %amount=2) or (name=concat('%02',' ','%03') and %amount=3) or (name=concat('%02',' ','%03',' ','%04') and %amount=4)"")
	getstring(""select number from skills where (name='%02' and %amount=2) or (name=concat('%02',' ','%03') and %amount=3) or (name=concat('%02',' ','%03',' ','%04') and %amount=4)"")
	if sql(""select 1 from skilltable where %string=skilltable.number and forwhom='%me'"")
		if sql(""select 1 from tmp_usertable where name='%me' and training=0"")
			say(""You do not have enough training sessions to train that skill.<BR>"")
			showstandard
		else
			sql(""update skilltable set skilllevel=skilllevel+1 where %string=number and forwhom='%me'"")
			sql(""update tmp_usertable set training=training-1 where name='%me'"")
			if sql(""select %amount = 2"")
				sayeveryone(""%me improves upon %02.<BR>"")
				say(""You improve upon %02.<BR>"")
			end
			if sql(""select %amount = 3"")
				sayeveryone(""%me improves upon %02 %03.<BR>"")
				say(""You improve upon %02 %03.<BR>"")
			end
			if sql(""select %amount = 4"")
				sayeveryone(""%me improves upon %02 %03 %04.<BR>"")
				say(""You improve upon %02 %03 %04.<BR>"")
			end
			showstandard
		end
	else
		say(""You do not have that skill, so you cannot practise.<BR>"")
		showstandard
	end
else
	say(""That particular skill is unknown.<BR>"")
	showstandard
end
return
" where id = 77;

END_OF_DATA