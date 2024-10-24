# ========================= Задание 2 =========================
chmod u=-,g=r,o=rw grumpig3
chmod u=rx,g=x,o=w magmar4
chmod 620 meditite3
chmod 500 onix0
chmod u=rx,g=x,o=w paras5
chmod uo=-,g=rw ponyta4

cd magmar4
chmod 006 slowking
chmod u=-,g=r,o=rw shelmet
chmod 777 nidoking
chmod u=rwx,go=wx golem
chmod 357 wormadam
chmod 361 prinplup

cd ../onix0
chmod ugo=r kirlia
chmod 624 drapion
chmod ugo=r pupitar
chmod 600 wigglytuff
chmod 404 eelektrik
chmod ug=rx,o=- clefable

cd ../paras5
chmod 046 weedle
chmod ug=rx,o=x elekid
chmod 357 glalie
chmod ug=wx,o=rwx skitty
chmod 622 claydol
chmod 577 bayleef

cd ..

echo -e "\nРезультат ls -lR, Задание 2:"
ls -lR