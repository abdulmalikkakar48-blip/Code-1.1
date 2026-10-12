package com.maree.app

/** Prepared English advice, with the same topic IDs and rules as the French catalogue. */
internal val coachAdviceTopicsEnglish: List<CoachTopic> = listOf(
    CoachTopic(
        id = "tiktok", label = "TikTok",
        keywords = listOf("tiktok", "tik tok", "tiktoc", "for you", "scroll tiktok"),
        replies = listOf(
            "Before opening TikTok, decide what you want to watch and when you will stop. When that time comes, put your phone down and move to another room.",
            "TikTok always has one more video ready. Try closing the app at the end of the video you are watching, then take a five-minute break.",
            "To spend less time on TikTok without deleting it, remove its shortcut from your home screen and turn off its notifications. Making it a little harder to open can help."
        ),
        appAliases = listOf("tiktok", "tik tok", "tiktok lite")
    ),
    CoachTopic(
        id = "instagram", label = "Instagram",
        keywords = listOf("instagram", "insta", "reels", "stories", "story"),
        replies = listOf(
            "On Instagram, keep messages separate from the feed: reply to the people you wanted to contact, then close the app before moving on to Reels.",
            "Choose a specific time to check Instagram. Outside that time, turn off the notifications that invite you back without a reason.",
            "If Reels keep you watching for a long time, try taking a break after the one you are watching. Keep your phone out of reach for a few minutes."
        ),
        appAliases = listOf("instagram", "insta", "instagram lite")
    ),
    CoachTopic(
        id = "youtube", label = "YouTube",
        keywords = listOf("youtube", "you tube", "youtub", "shorts", "autoplay", "auto play"),
        replies = listOf(
            "On YouTube, choose a video before opening the feed. Turn off autoplay so the end of that video becomes a clear stopping point.",
            "After watching Shorts, have a very short activity ready: drink some water or walk a few steps. Start as soon as you close YouTube.",
            "Save interesting videos to a watch-later list. Choose just one now and leave the others for another time."
        ),
        appAliases = listOf("youtube", "you tube", "youtube go")
    ),
    CoachTopic(
        id = "snapchat", label = "Snapchat",
        keywords = listOf("snapchat", "snap", "snap streak", "snap map", "snapstreaks"),
        replies = listOf(
            "On Snapchat, keep the conversations that matter and silence the other alerts. You do not need to respond to every notification.",
            "If Snapstreaks bring you back when you do not want to, tell a friend you are taking a break. An app streak is not an obligation to them.",
            "Try checking Snapchat at a chosen time instead of after every alert. Reply to your messages, then close it before opening suggested content."
        ),
        appAliases = listOf("snapchat", "snap")
    ),
    CoachTopic(
        id = "whatsapp", label = "WhatsApp",
        keywords = listOf("whatsapp", "what sapp", "whatsapp group", "whatsapp groups", "whatsapp messages"),
        replies = listOf(
            "On WhatsApp, mute groups that often interrupt your day. Keep important calls or messages available according to your needs.",
            "You can tell people close to you that you will reply at a specific time. That can make a break easier without cutting off useful conversations.",
            "Before opening WhatsApp, choose the conversation you came to read. Reply to that person, then return to what you were doing."
        ),
        appAliases = listOf("whatsapp", "whatsapp business")
    ),
    CoachTopic(
        id = "games", label = "Games",
        keywords = listOf("games", "video game", "video games", "gaming", "free fire", "roblox", "fortnite", "minecraft", "brawl stars", "pubg"),
        replies = listOf(
            "Before starting a game, choose a clear finish: one match or a specific time. Tell your friends before you start so it is easier to leave.",
            "The end of a match is a good time for a break. Stand up before starting another one: you can then decide more calmly whether to continue.",
            "Keep time for games, but plan what comes afterwards too. For example: one match, then a ten-minute walk or a short task."
        ),
        appAliases = listOf("free fire", "roblox", "fortnite", "minecraft", "brawl stars", "pubg", "clash of clans", "candy crush")
    ),
    CoachTopic(
        id = "wake_up", label = "Waking up",
        keywords = listOf("wake up", "waking up", "in the morning", "first thing in the morning", "get out of bed"),
        replies = listOf(
            "Tomorrow morning, try doing one thing before opening a social app: drink some water, open the curtains or get ready.",
            "Tonight, plan a small screen-free start to tomorrow. Even five minutes before your first check can begin a new habit.",
            "If you check your phone as soon as you wake up, leave it a little further away tonight. Keep your alarm audible and start by getting out of bed."
        )
    ),
    CoachTopic(
        id = "meals", label = "Mealtimes",
        keywords = listOf("meal", "meals", "eating", "lunch", "dinner", "breakfast", "at the table"),
        replies = listOf(
            "For your next meal, put your phone away from the table. Just try leaving it there until the end, without aiming for the whole day.",
            "Choose one screen-free meal today. If you are eating with someone, suggest putting your phones away together.",
            "If eating without a screen feels difficult, start with the first five minutes. Notice your meal or talk to the person beside you."
        )
    ),
    CoachTopic(
        id = "evening", label = "Evenings",
        keywords = listOf("evening", "in the evening", "end of the day", "after dinner", "getting home"),
        replies = listOf(
            "For tonight, plan an activity before settling down with your phone: listen to music, read or tidy up for ten minutes.",
            "Choose a time to check your phone and a time to put it away this evening. Then leave it somewhere other than in your hand.",
            "An evening with less screen time can start with a small break. Try twenty minutes for an activity you enjoy away from your phone."
        )
    ),
    CoachTopic(
        id = "sleep", label = "Sleep",
        keywords = listOf("sleep", "sleeping", "fall asleep", "bedtime", "insomnia", "sleepless night", "can t sleep", "cannot sleep", "can t fall asleep", "trouble sleeping", "unable to sleep"),
        replies = listOf(
            "Try choosing a time to put your phone away before sleep. Have another calm activity ready, such as reading a few pages or listening to music.",
            "Tonight, avoid bringing a video feed into bed. Leave your phone on a table further away and keep what you need for the night nearby.",
            "You can start with a short screen break before bed. If sleep difficulties continue or worry you, ask a healthcare professional for advice."
        )
    ),
    CoachTopic(
        id = "work", label = "Work",
        keywords = listOf("work", "working", "office", "meeting", "concentration", "productivity", "focus"),
        replies = listOf(
            "Choose one specific task and spend ten minutes on it with your phone out of reach. You can then decide whether to continue or take a break.",
            "Before working, turn off unnecessary alerts and keep only the contacts you need. Set a time to check your messages.",
            "Write your next small work action on paper. When you want to open a social app, return to that action for two minutes."
        )
    ),
    CoachTopic(
        id = "school", label = "School and studying",
        keywords = listOf("school", "high school", "college", "class", "homework", "studying", "study", "revision", "exams"),
        replies = listOf(
            "When studying, start with one exercise or page with your phone put away. Take a break after that small goal rather than halfway through.",
            "If you need your phone for a class, open only the document you need. Write down other things you want to check and save them for your break.",
            "Prepare a short homework session: materials ready, one task chosen and notifications off. Ten minutes is enough to get started."
        )
    ),
    CoachTopic(
        id = "boredom", label = "Boredom",
        keywords = listOf("boredom", "bored", "boring", "nothing to do", "don t know what to do", "do not know what to do"),
        replies = listOf(
            "When you are bored, choose a two-minute activity: draw, tidy a small area or walk a few steps. You do not need a big project.",
            "Make a short list of enjoyable things to do away from a screen. When boredom arrives, try the easiest one before opening a feed.",
            "You can also let boredom pass for a moment. Put your phone down, look around and then choose what you actually want to do."
        )
    ),
    CoachTopic(
        id = "stress", label = "Stress",
        keywords = listOf("stress", "stressed", "anxiety", "anxious", "pressure", "tense", "panic"),
        replies = listOf(
            "If you reach for your phone when stressed, try a short pause first: put it down, breathe comfortably and name what is worrying you.",
            "Choose one small practical thing you can do now. A message to someone you trust or a few minutes of walking can be a start.",
            "A video feed can distract you for a while without resolving what is troubling you. If stress is hard to live with or keeps returning, talk to someone you trust or a professional."
        )
    ),
    CoachTopic(
        id = "loneliness", label = "Loneliness",
        keywords = listOf("loneliness", "lonely", "alone", "isolated", "nobody to talk to", "no one to talk to", "need company"),
        replies = listOf(
            "If you are looking for company on your phone, try a direct conversation: suggest a call or a short outing to someone you know.",
            "You could send someone a simple message: “Do you have a few minutes to talk?” A conversation you choose can feel better than a long feed.",
            "Choose a small opportunity to see people: an activity, someone close to you or a place you enjoy. If loneliness feels heavy, talk to someone you trust."
        )
    ),
    CoachTopic(
        id = "fomo", label = "Fear of missing out",
        keywords = listOf("fomo", "fear of missing out", "afraid of missing", "missing something", "miss something", "know everything", "stay up to date"),
        replies = listOf(
            "You can choose when to catch up instead of checking constantly. Set your next checking time and silence unnecessary alerts until then.",
            "Ask yourself what would be truly urgent. Most other things can wait until later; keep a way for important people to contact you.",
            "Try a short ten-minute break without checking. When you return, notice what actually needed your attention."
        )
    ),
    CoachTopic(
        id = "tiredness", label = "Tiredness",
        keywords = listOf("tired", "tiredness", "exhausted", "fatigue", "no energy", "out of energy"),
        replies = listOf(
            "When you are tired, choose a low-effort break: put your phone down, drink some water or sit quietly for a few minutes.",
            "You do not need to fill every break with a screen. Try a few minutes without content, then decide what you need.",
            "For today, keep your screen-time goal simple rather than making a big change. If unusual tiredness lasts or worries you, ask a healthcare professional for advice."
        )
    ),
    CoachTopic(
        id = "notifications", label = "Notifications",
        keywords = listOf("notifications", "notification", "alerts", "alert", "phone rings", "vibrates", "vibration"),
        replies = listOf(
            "In Android settings, find Notifications and choose an app that often interrupts you. Turn off one unnecessary category while keeping important alerts.",
            "Start with notifications for recommendations, promotions or new content. You can still open the app at the time you choose.",
            "Try a period in Do Not Disturb mode. Check its exceptions so the calls or contacts you need can still get through."
        )
    ),
    CoachTopic(
        id = "grayscale", label = "Grayscale screen",
        keywords = listOf("black and white", "grayscale", "greyscale", "grey screen", "gray screen", "no colors", "no colours"),
        replies = listOf(
            "On a Samsung phone, open Settings and search for “grayscale”. Depending on the version, the setting may be under Accessibility or a bedtime mode.",
            "Try grayscale for a short period to see whether feeds feel less tempting. It is not a block, and you can switch back to color.",
            "Search for “grayscale” in your phone settings. Its name and location vary between Android versions; I do not change this setting for you."
        )
    ),
    CoachTopic(
        id = "bedroom", label = "Phone in the bedroom",
        keywords = listOf("bedroom", "in bed", "near the bed", "away from bed", "bedside table", "sleep with my phone"),
        replies = listOf(
            "Choose a place to charge your phone away from your bed. If you use it as an alarm, make sure you can hear it or arrange another alarm.",
            "Try keeping your bed for rest rather than browsing a feed. Start by leaving your phone on a table out of reach.",
            "You can try one night with your phone outside the bedroom. Keep the contact methods or alarm you actually need available."
        )
    ),
    CoachTopic(
        id = "uninstall", label = "Uninstalling an app",
        keywords = listOf("uninstall", "uninstalling", "delete an app", "delete application", "remove an app", "remove from my phone"),
        replies = listOf(
            "Before uninstalling, check whether you want to keep any files or sign-in details. On Android, a long press on the icon often gives access to Uninstall.",
            "You can start by removing the home-screen shortcut and turning off notifications. If that is not enough, decide whether to uninstall it.",
            "Removing an app from Selkie tracking does not uninstall it. To delete it from your phone, use that app’s Android settings."
        )
    ),
    CoachTopic(
        id = "distance", label = "Putting your phone away",
        keywords = listOf("out of reach", "away from me", "another room", "put my phone away", "put my phone down", "phone far away"),
        replies = listOf(
            "Choose a specific place to leave your phone during your next activity. Keeping it out of reach helps you avoid picking it up without thinking.",
            "Try ten minutes with your phone in another room. Keep necessary calls audible if you need them.",
            "When you finish checking your phone, put it in the same place. This small routine can make leaving a feed easier."
        )
    ),
    CoachTopic(
        id = "breaks", label = "Taking a break",
        keywords = listOf("take a break", "screen break", "screen free break", "phone break", "disconnect", "unplug"),
        replies = listOf(
            "Choose a short break and do something with your hands or feet: walk, draw or make a drink. You can start with five minutes.",
            "For your next break, try leaving your phone down. A break from work does not need to become another screen session.",
            "Prepare a simple way to leave your phone: close the app, stand up and walk a few steps. You can then decide whether to return."
        )
    ),
    CoachTopic(
        id = "exceeded", label = "Going over a limit",
        keywords = listOf("limit exceeded", "exceeded my limit", "over my limit", "limit reached", "scrolled too much", "used too much", "exceeded again"),
        replies = listOf(
            "Going over a limit is a chance to take a break now. Close the app, put your phone down and choose a short activity away from a screen.",
            "Try noticing the moment you continued longer than planned. Next time, prepare a clear stopping point: the end of a video, a message or a match.",
            "You can start again with a small goal for the rest of the day. If Selkie has locked the app, respect the break rather than immediately trying to reset it."
        )
    ),
    CoachTopic(
        id = "relapse", label = "Starting again after a setback",
        keywords = listOf("relapse", "relapsed", "scrolling again", "missed my goal", "failed", "feel guilty", "messed up", "gave in", "setback"),
        replies = listOf(
            "A difficult day does not erase your earlier efforts. Choose one small action now, such as putting your phone down for five minutes.",
            "Instead of judging yourself, look at what made the break difficult: an alert, boredom or tiredness. Prepare one simple support for next time.",
            "Start again with an easier goal today. You can also choose a standard challenge; its five days do not need to be consecutive."
        )
    ),
    CoachTopic(
        id = "streak", label = "Successful days",
        keywords = listOf("day streak", "successful days", "consecutive days", "keep my streak", "broke my streak", "streak", "consistency"),
        replies = listOf(
            "To keep a habit going, prepare a small action you can repeat even on busy days. A less successful day does not stop you from starting again tomorrow.",
            "Standard challenges need five different dates, and you can leave days between them. You do not need a perfect streak of consecutive days to finish.",
            "Notice what helped on the days you kept your break, then repeat that detail. Intermediate challenge days do not add points: bonuses follow the rules for completed challenges."
        )
    ),
    CoachTopic(
        id = "start", label = "Getting started",
        keywords = listOf("start", "begin", "where to start", "first step", "hello", "hi", "help me", "coach", "who are you", "artificial intelligence"),
        replies = listOf(
            "I offer prepared advice on your phone, rather than replies from a generative AI. To begin, choose a topic: an app, sleep or a short break.",
            "Start with just one habit: choose a time to put your phone down or track one app in Selkie. Tell me what feels most difficult.",
            "My help is based on topics and replies written in advance. You can ask about TikTok, challenges, your points or a time when you want to check your phone less."
        )
    ),
    CoachTopic(
        id = "goals", label = "Choosing a goal",
        keywords = listOf("goal", "goals", "scrolling", "scroll", "scroll less", "mindless scrolling", "stop scrolling", "reduce screen time", "spend less time", "good habit", "change my habits", "want to watch", "can t stop", "cannot stop"),
        replies = listOf(
            "Choose a specific goal for today: a screen-free meal, ten minutes of reading or checking your phone at a set time. Keep it small enough to try.",
            "Describe your goal with a time and an action: “After eating, I will put my phone down and walk for five minutes.” That makes it easier to follow.",
            "To reduce scrolling, prepare what you will do instead. A break becomes easier when a concrete activity is waiting for you."
        )
    ),
    CoachTopic(
        id = "success", label = "A success",
        keywords = listOf("i did it", "goal achieved", "i managed", "proud of myself", "used less today", "victory", "progress"),
        replies = listOf(
            "Take a moment to acknowledge this success. Note what helped today so you can do it again tomorrow.",
            "Keep the same small goal for a little longer before making it harder. A useful habit also grows by repeating what works.",
            "Enjoy the time you have regained by doing something you like. I do not add points through this conversation: rewards follow Selkie’s rules."
        )
    )
)
