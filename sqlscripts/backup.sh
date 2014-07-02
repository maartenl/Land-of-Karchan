mysqldump -u root -pmysecretpasswordthatnobodyknows --ignore-table=mmud.mm_commandlog mmud > landofkarchan.`date +%F`
tar zcvf landofkarchan.`date +%F`.tgz landofkarchan.`date +%F`
rm landofkarchan.`date +%F`
