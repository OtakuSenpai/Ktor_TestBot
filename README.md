# Ktor_TestBot
A test bot which connects to IRC with Ktor and Coroutines, using Raw Sockets!!!

This bot is a simple exercise in showing how to use Ktor with raw sockets and coroutines. The
BasicConnection class is where all the connecting stuff happens. main.kt is used to implement 
the test bot. The bot connects to any IRC network and joins a channel. To see 
the bot, connect to the network and type "/join #channel-name". However the bot doesn't do much.

## Run

Do this following commands from the teminal:-
```
git clone --depth=1 https://github.com/OtakuSenpai/Ktor_TestBot.git
cd Ktor_TestBot
gradle build
gradle fatJar
```

The last command creates a fatJar which will be located in ${Project_Root}/build/libs.

Made by Avra Neel Chakraborty aka OtakuSenpai, under public domain!!!
