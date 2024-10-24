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