# ========================= Задание 1 =========================
mkdir -p lab0/magmar4 lab0/onix0 lab0/paras5
cd lab0
echo "Живёт Forest Grassland Taiga" > grumpig3
echo "Развитые способности Telepathy" > meditite3
echo -e "Возможности Overland=12 Jump=4 Power=2\nIntelligence=4 Egg Warmer=0 Firestarter=0 Glow=0 Heater=0 Sinker=0" > ponyta4

cd magmar4
mkdir nidoking golem wormadam prinplup
echo -e "weigth=175.3\nheight=79.0 atk=8 def=8" > slowking
echo -e "Тип диеты\nHerbivore" > shelmet

cd ../onix0
mkdir clefable
cat > kirlia <<EOF
Тип
покемона PSYCHIC NONE
EOF
cat > drapion <<EOF
Развитые способности Keen
eye
EOF
cat > pupitar <<EOF
weigth=335.1 height=47.0 atk=8
def=7
EOF
cat > wigglytuff <<EOF
Возможности Overland=7 Surface=5 Jump=3 Power=2
Intelligence=4 Inflatable=0
EOF
echo -e "satk=8 sdef=7\nspd4" > eelektrik

cd ../paras5
mkdir elekid glalie skitty bayleef
echo "Живёт Forest" > weedle
echo -e "Тип диеты\nTerravore" > claydol

cd ..

echo -e "\nРезультат ls -lR, Задание 1:"
ls -lR
# ========================= Задание 2 =========================
chmod u=-,g=-,o=r grumpig3
chmod u=rx,go=wx magmar4
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
chmod 561 prinplup

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
# ========================= Задание 3 =========================
cp -R onix0 paras5/skitty
chmod u+w magmar4
ln -s $(realpath meditite3) magmar4/slowkingmeditite
chmod u-w magmar4
cp meditite3 magmar4/nidoking
chmod u+r paras5/weedle
cat paras5/claydol paras5/weedle > ponyta4_28
chmod u-r paras5/weedle
chmod u+w onix0
ln meditite3 onix0/eelektrikmeditite
ln -s $(realpath onix0) Copy_26
cat meditite3 > onix0/kirliameditite
chmod u-w onix0

echo -e "\nРезультат ls -lR, Задание 3:"
ls -lR
# ========================= Задание 4 =========================
echo -e "\nЗадание 4:"

wc -l magmar4/shelmet onix0/kirlia onix0/drapion onix0/pupitar 2>/tmp/temp.tmp | sort -n
ls  -lt -ur **/s* 2>/dev/null | grep -e "^-" -e "^l"
cat -n $(ls **/c*) 2>>1 | sort -r
cat -n meditite3 2>/tmp/temp.tmp | sort -k2
ls -lRt 2>/dev/null| tail +2 | head -n 3
ls -lRtr -u paras5
# ========================= Задание 5 =========================
chmod u+w grumpig3
rm grumpig3
chmod -R u+rw onix0
rm onix0/eelektrik
rm Copy_*
rm onix0/eelektrikmediti*
rm -R onix0
chmod u+w magmar4
chmod u+r magmar4/wormadam
rm -R magmar4/wormadam
chmod u-w magmar4

echo -e "\nРезультат ls -lR, Задание 5:"
ls -lR