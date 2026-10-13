package com.maree.app

/** Full English reference answers. Future services are clearly separated from this test version. */
internal val selkieHelpEntriesEnglish: List<SelkieHelpEntry> = listOf(
    englishHelp(
        SelkieHelpSection.DISCOVER, "app_overview", "What is Selkie for?",
        listOf("selkie", "maree", "what is selkie for", "what is selkie", "about selkie", "how does selkie work", "app description", "what does this app do", "what is this app for", "how does the app work", "how to use selkie"),
        "Selkie helps you find more time away from screens: choose apps to track, set their limits and complete challenges over several days. " +
            "Tracking can display a break screen when a limit is reached. Points encourage consistency, and the Coach offers prepared advice and app help, free and offline."
    ),
    englishHelp(
        SelkieHelpSection.DISCOVER, "navigation", "Where can I find each section?",
        listOf("navigation", "sections", "tabs", "where to find", "icons", "bottom buttons", "home", "more menu", "top menu", "hamburger menu", "three lines", "app screens"),
        "The four tabs are at the bottom: Home for your apps and points; Daily for a break idea and your overview; Challenges to complete your daily steps; Coach for advice and help. " +
            "The ☰ menu at the top groups Language, Appearance, Android access, Subscriptions, Selkie rules, and Privacy and data."
    ),
    englishHelp(
        SelkieHelpSection.DISCOVER, "daily", "What is in the Daily tab?",
        listOf("daily", "daily overview", "break of the day", "daily tip", "daily summary", "what does this overview measure", "daily exercises", "daily activities", "daily routine"),
        "Daily suggests a break idea without points or a completion check, adds up the counters for tracked apps and shows your challenge progress. " +
            "This overview does not measure all your screen time. Counters may restart after adding an app, resetting a limit or the end of a lock; tracking must be active to update them."
    ),
    englishHelp(
        SelkieHelpSection.DISCOVER, "coach_help", "How does the Coach work?",
        listOf("coach", "ai coach", "ai", "chatgpt", "artificial intelligence", "how does the coach work", "free coach", "offline coach", "prepared replies"),
        "Write your question in Coach or choose a help topic. This coach recognizes words and phrases to offer prepared advice and explain Selkie. " +
            "Standard input and help work free on your phone, without an account or Internet. The library offers ${PlanType.FREE.coachQuestionCount} prepared questions in Free, ${PlanType.PREMIUM.coachQuestionCount} in Premium and ${PlanType.ULTRA.coachQuestionCount} in Ultra. This coach does not understand every question and it is not ChatGPT."
    ),
    englishHelp(
        SelkieHelpSection.DISCOVER, "coach_unknown", "What if the Coach does not understand?",
        listOf("coach does not understand", "coach doesn t understand", "unknown question", "unrecognized phrase", "unrecognized questions", "save my questions", "coach history", "saved conversation"),
        "If the Coach does not recognize your phrase, it offers topic buttons: choose one or rephrase. The full conversation is not archived. " +
            "You can enable local storage of unrecognized phrases in the Coach options: up to 50 phrases of 800 characters, with nothing sent to the publisher. " +
            "You can view or delete them; turning the option off also deletes them. Sensitive-message filters may be imperfect: avoid private information."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "add_app", "Add an app to tracking",
        listOf("add an application", "add an app", "add to tracking", "track an application", "track an app", "add button", "select an app"),
        "In Home, tap Add, choose an available app and then set its limit in 5-minute steps. You need more than 0 points to add it. " +
            "Free allows 10 apps, Premium 50 and Ultra has no commercial cap on compatible apps. If an app is missing, it may not be compatible or accessible in the Android list."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "limits", "Set or change a limit",
        listOf("set a limit", "choose a limit", "change a limit", "change my limit", "change the limit", "daily limit", "how many minutes", "adjust a limit"),
        "When adding an app, choose a limit that suits your use: from 5 to 1,440 minutes, in 5-minute steps. To change it, tap Change limit on its card in Home. " +
            "Changing a limit requires a balance above 0 points, even with Premium or Ultra. Changing the limit does not lift an active lock: wait for it to end or reset it for 10 points."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "red_screen", "Why is an app blocked?",
        listOf("red screen", "break screen", "blocked screen", "blocking screen", "lock", "blocked app", "blocked for 24 hours", "block duration"),
        "When a tracked app reaches its limit, active tracking creates a 24-hour lock starting at that moment. The break screen appears when you open that app, provided the permissions are granted. " +
            "Home shows the time remaining. Wait for the lock to end or reset it for 10 points. Selkie is a personal aid: it is not a block that is impossible to bypass in Android."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "reset", "Reset a limit",
        listOf("reset", "resetting", "lift the lock", "unlock", "reset the counter", "restart my limit", "i want to reset", "how to reset"),
        "In Home, open the app’s ⋮ menu, choose Reset and confirm. You need at least 10 points; 10 points are deducted, the lock is lifted and the counter restarts with the full limit. " +
            "This does not delete Android statistics or an overrun already recorded for the discipline bonus. The cost is the same on Free, Premium and Ultra."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "remove_app", "Remove an app from tracking",
        listOf("remove from tracking", "remove an app from tracking", "delete from tracking", "stop tracking", "stop tracking an app"),
        "In Home, open the app’s ⋮ menu, choose Remove from tracking and confirm. Its entry, limit and lock are removed from Selkie; the app stays installed on your phone. " +
            "You can remove it even at 0 points. This does not refund points or erase an overrun already recorded for the discipline bonus."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "permissions", "Which permissions are needed?",
        listOf("permission", "permissions", "authorization", "usage access", "usage data access", "overlay", "display over other apps", "appear on top"),
        "Usage access measures time and identifies the app in use; permission to display over other apps shows the break screen. " +
            "Both are required to enable tracking. Set them on the initial welcome screen or in the ☰ menu → Android access → Turn on tracking / Manage permissions. You can explore the app and use Challenges and Coach without enabling tracking."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "notifications_permission", "Are alerts required?",
        listOf("selkie alerts", "selkie notifications", "allow notifications", "deny notifications", "optional alerts", "before the limit", "80 percent", "warn before the limit"),
        "Notifications are optional. When allowed, Selkie can warn you when about 80% of the limit has been used and make its tracking service visible. " +
            "Without them, tracking and the break screen can still work with the two required permissions. In the ☰ menu → Android access, choose Turn on optional alerts, or adjust their visibility in Android settings."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "battery", "Tracking stops or uses battery",
        listOf("battery", "power saving", "battery saver", "tracking stops", "tracking does not work", "tracking doesn t work", "blocking does not work", "background tracking", "battery optimization"),
        "In the ☰ menu → Android access, check that tracking is active and both Android permissions are granted. Some phones restrict background apps: in Settings → Apps → Selkie → Battery, " +
            "check the restrictions for your model, then reopen Selkie. A tracking service uses resources; its battery use and continuity depend on the phone. You can pause it in the ☰ menu → Android access."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "pause_tracking", "Pause or resume tracking",
        listOf("pause tracking", "tracking pause", "stop tracking service", "disable tracking", "resume tracking", "enable tracking", "tracking paused"),
        "In the ☰ menu → Android access, choose Pause tracking: monitoring and break screens stop, but your saved apps, limits and locks are kept. " +
            "To resume, choose Turn on tracking and check the Android permissions. On resuming, Android may provide usage for the day: pausing the service does not guarantee that all those minutes are excluded from the counters."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "usage", "What do the displayed minutes measure?",
        listOf("my usage", "tracked usage", "usage counters", "screen time", "how many minutes", "minutes used", "usage today", "statistics", "see my usage", "counter", "displayed time"),
        "Home shows each tracked app’s counter, and Daily adds them up. Selkie uses Android events from the day, with a reference reset when an app is added, a limit is reset or a lock ends. " +
            "These counters therefore do not always match Android’s total or all your screen time. They depend on permissions, active tracking and the information provided by your phone."
    ),
    englishHelp(
        SelkieHelpSection.TRACKING, "compatibility", "Which phones and apps are compatible?",
        listOf("compatible", "compatibility", "android 10", "samsung", "a16", "iphone", "ios", "app not found", "app missing", "missing app"),
        "This version is for Android 10 or newer, including Samsung Galaxy A16 phones. There is no active iPhone version. " +
            "The tracking list shows apps accessible from the Android launcher, except Selkie. An app without a launcher icon may be missing. Time measurements and break screens can vary with Android and manufacturer restrictions."
    ),
    englishHelp(
        SelkieHelpSection.POINTS, "points", "What are my points for?",
        listOf("points", "balance", "discipline points", "honor points", "honour points", "how many points", "point rules"),
        "Open the ☰ menu → Selkie rules to review the rules. You start with 100 points. They reflect your progress and Selkie’s rules: resetting costs 10 points; a detected reopening of a locked app costs 5 points. " +
            "You can earn points through the discipline bonus and challenges. The balance cannot fall below zero; at 0 points, adding an app or changing a limit is blocked on every plan. Points are not money."
    ),
    englishHelp(
        SelkieHelpSection.POINTS, "earn_points", "How can I earn points?",
        listOf("earn points", "earn back points", "get points back", "get points", "increase my points", "rewards", "point bonus"),
        "There are three ways: +10 points per 24-hour period without a recorded overrun; +5 points for every group of five fully completed standard challenges; " +
            "+0.25 points once at the end of a custom challenge on Premium or Ultra. Intermediate days do not award points, and writing to the Coach does not add any."
    ),
    englishHelp(
        SelkieHelpSection.POINTS, "lose_points", "Why did I lose points?",
        listOf("lose points", "lost points", "point loss", "fewer points", "points deducted", "penalty", "reopen an app", "open a locked app again"),
        "Resetting a limit deducts 10 points after confirmation. A detected reopening of an app that is still locked can deduct 5 points; " +
            "these penalties are at least 30 seconds apart for the same app. Reaching the limit alone does not deduct 5 points, but records an overrun that prevents the discipline bonus for that period."
    ),
    englishHelp(
        SelkieHelpSection.POINTS, "zero_points", "What can I do at 0 points?",
        listOf("zero points", "0 points", "no points left", "no points", "stuck at zero", "at zero", "zero balance"),
        "At 0 points, you cannot add an app or change a limit, even with Premium or Ultra. Resetting requires at least 10 points. " +
            "You can still remove an app, pause tracking, use the Coach and complete challenges to earn points back. The discipline bonus can also add points if no overrun is recorded during the period."
    ),
    englishHelp(
        SelkieHelpSection.POINTS, "discipline_bonus", "When do I get the discipline bonus?",
        listOf("discipline bonus", "daily bonus", "10 points", "ten points", "24 hour bonus", "discipline period", "bonus without overrun"),
        "The bonus is 10 points at the end of a 24-hour period started by Selkie, if no overrun or penalized reopening was recorded during that period. " +
            "It is not necessarily at midnight. The calculation updates during tracking or when you open the app, catching up on at most seven periods. Removing an app or resetting it does not erase an already recorded overrun."
    ),
    englishHelp(
        SelkieHelpSection.CHALLENGES, "challenges", "How do I complete a suggested challenge?",
        listOf("challenges", "challenge", "complete a challenge", "read 10 pages", "five days", "5 days", "suggested challenges", "challenge bonus"),
        "In Challenges → Suggested challenges, do the activity and then confirm that day. A challenge requires five different dates, with at most one confirmation per day; the days can be spaced apart. " +
            "Intermediate days do not award points. For every group of five fully completed challenges, you earn 5 points; your completed-challenge total continues between sets."
    ),
    englishHelp(
        SelkieHelpSection.CHALLENGES, "challenge_cycle", "When do suggested challenges change?",
        listOf("challenge set", "new set", "challenge renewal", "challenges change", "21 days", "twenty one days", "progress reset", "missed a day"),
        "Selkie offers ten challenges per set, from two groups that alternate every 21 days. At this change, confirmations and progress for the current set are reset. " +
            "Your points and total of fully completed challenges are kept. A missed day does not reset a challenge before the change. Custom challenges are not reset by this change."
    ),
    englishHelp(
        SelkieHelpSection.CHALLENGES, "custom_challenges", "Create and finish my own challenge",
        listOf("custom challenge", "custom challenges", "create a challenge", "create my challenge", "my own challenges", "my challenges", "three steps", "3 steps", "0 25 points"),
        "With Premium or Ultra, open Challenges → My challenges: choose a title, exactly three steps and a duration of 2 to 365 days. You can have ten active custom challenges. " +
            "Each day, check all three steps and then confirm; there is one confirmation per date, and days can be spaced apart. The challenge awards 0.25 points in total, once at the end. On Free, continuing it requires switching back to a plan that allows custom challenges."
    ),
    englishHelp(
        SelkieHelpSection.CHALLENGES, "challenge_photos", "Do I need a photo to confirm a challenge?",
        listOf("proof", "photo", "photos", "take a photo", "prove the challenge", "challenge proof", "camera"),
        "No: this version does not require a photo to confirm a challenge. Completions are declared by the user and are not automatically verified. " +
            "Selkie does not request access to your photos or camera for challenges. Only confirm days when you completed the activity."
    ),
    englishHelp(
        SelkieHelpSection.OFFERS, "premium", "What does Premium include?",
        listOf("premium", "plans", "free plan", "free", "plan differences", "how many apps", "app limit", "premium price", "subscription", "subscriptions", "plan prices", "subscription prices"),
        "Find the plans in the ☰ menu → Subscriptions. Free allows 10 tracked apps. Premium is planned at " +
            "€${PlanType.PREMIUM.monthlyPriceEuros}/month or €${PlanType.PREMIUM.annualPriceEuros}/year, with 50 apps and custom challenges. " +
            "Ultra is planned at €${PlanType.ULTRA.monthlyPriceEuros}/month or €${PlanType.ULTRA.annualPriceEuros}/year, with no commercial cap on compatible apps. " +
            "The prepared library includes ${PlanType.FREE.coachQuestionCount} questions in Free, ${PlanType.PREMIUM.coachQuestionCount} in Premium and ${PlanType.ULTRA.coachQuestionCount} in Ultra. Standard input, Coach help and suggested challenges are included in every plan. Selecting a plan here is a local demo: no subscription or charge is created. Point rules stay the same, including at 0 points."
    ),
    englishHelp(
        SelkieHelpSection.OFFERS, "ultra", "What does Ultra include?",
        listOf("ultra", "ultra premium", "ultra price", "unlimited apps", "no cap", "tracking sort", "tracking search", "unlimited coach"),
        "Ultra is planned at €${PlanType.ULTRA.monthlyPriceEuros}/month or €${PlanType.ULTRA.annualPriceEuros}/year: no commercial cap on compatible apps, " +
            "custom challenges, search and sorting for tracking, and ${PlanType.ULTRA.coachQuestionCount} prepared Coach question-and-answer pairs. " +
            "No payments are possible in this test version, and Ultra does not bypass point rules."
    ),
    englishHelp(
        SelkieHelpSection.OFFERS, "subscription_monthly", "How would the monthly subscription work?",
        listOf("monthly", "every month", "each month", "per month", "monthly subscription", "monthly renewal", "payment frequency"),
        "The planned monthly price is €${PlanType.PREMIUM.monthlyPriceEuros} for Premium or €${PlanType.ULTRA.monthlyPriceEuros} for Ultra, billed each month. " +
            "Once activated, it will renew automatically unless canceled; Google Play will show the price, date and terms before confirmation. " +
            "No subscription, billing or renewal is active in this version."
    ),
    englishHelp(
        SelkieHelpSection.OFFERS, "subscription_annual", "What is the annual plan price?",
        listOf("annual", "yearly", "per year", "each year", "every year", "annual subscription", "discount", "120 euros", "240 euros"),
        "The planned annual price is €${PlanType.PREMIUM.annualPriceEuros} for Premium or €${PlanType.ULTRA.annualPriceEuros} for Ultra, paid once for a year. " +
            "This equals twelve monthly payments, with the same features. " +
            "It will renew each year unless canceled once payments are activated. No annual subscription is sold here."
    ),
    englishHelp(
        SelkieHelpSection.OFFERS, "cancel_subscription", "How do I stop a subscription?",
        listOf("cancel", "cancellation", "cancel my subscription", "cancel the subscription", "stop my subscription", "stop the subscription", "unsubscribe", "uninstall selkie cancel"),
        "No real subscription is created in this version. For a future subscription purchased on Google Play: open Play Store → your profile picture → Payments & subscriptions → Subscriptions → Selkie → Cancel subscription. " +
            "This stops the next renewal; access usually remains available until the end of the period already paid for. Uninstalling Selkie or deleting its data does not cancel a subscription. Cancelling or a month already used does not mean an automatic refund. Mandatory rights remain applicable: see Privacy and data → Subscriptions and cancellation."
    ),
    englishHelp(
        SelkieHelpSection.OFFERS, "restore_subscription", "Can I restore a purchase?",
        listOf("restore", "restoration", "restore my purchase", "restore subscription", "recover my subscription", "already paid", "purchase not found", "premium disappeared"),
        "No real purchase or subscription restoration is active in this version: the selected plan is only saved on the phone for testing. " +
            "Future restoration must verify the purchase with the Google Play account used and server-side entitlements. It will not automatically restore your points, limits or challenges: that data backup does not yet exist."
    ),
    englishHelp(
        SelkieHelpSection.OFFERS, "payments", "How will subscriptions be paid for?",
        listOf("pay", "payment", "payments", "charge", "billing", "bank card", "credit card", "google play billing", "google pay", "stripe", "buy a subscription"),
        "No payments are possible in this version, and trying a demo plan charges nothing. For digital subscriptions in the future Play Store version, " +
            "the planned integration is Google Play Billing, with server-side purchase validation. Google Pay is a separate payment wallet. Selkie should not ask for your card number in the Coach."
    ),
    englishHelp(
        SelkieHelpSection.ACCOUNT, "account", "Do I need to create an account?",
        listOf("account", "sign in", "log in", "login", "registration", "create an account", "open an account", "password", "username", "sign up"),
        "You can use Selkie and its local Coach without an account. Account creation and server sign-in are not active yet. " +
            "An account is planned to help manage subscription entitlements and changing phones; tracking data stays local in this version. " +
            "A Selkie account alone is neither a payment nor a backup, and Google Play can manage a purchase without a separate Selkie account."
    ),
    englishHelp(
        SelkieHelpSection.ACCOUNT, "single_device", "Can I use the account on several phones?",
        listOf("several phones", "multiple phones", "several devices", "multiple devices", "two phones", "two devices", "one device only", "one phone only", "share my account", "shared account", "multi device"),
        "The planned rule is one active phone per Selkie account: activating a new phone must replace the previous one, with no simultaneous use of the same account. " +
            "This restriction requires accounts and server verification; it is not enforced yet. In the current version, each installation keeps its own local data and no shared account synchronizes it."
    ),
    englishHelp(
        SelkieHelpSection.ACCOUNT, "change_phone", "What happens if I change phones?",
        listOf("change phones", "change phone", "new phone", "another phone", "transfer", "backup", "synchronization", "sync", "recover my data", "migrate"),
        "For now, your points, apps and challenges stay on the phone: Selkie has no active server backup or synchronization, and its Android backup is disabled. " +
            "Changing devices can therefore lose this progress. A future account must allow replacing the active device and checking any subscription; transferring tracking data must be developed separately."
    ),
    englishHelp(
        SelkieHelpSection.PRIVACY, "privacy", "Where are my data and privacy information?",
        listOf("privacy", "privacy policy", "personal data", "my data", "gdpr", "cnil", "controller", "first and last name", "full name", "my real name", "private life", "messages read"),
        "Read the policy in the ☰ menu → Privacy and data. Your tracked apps, limits, points and challenges stay on your phone; the local Coach does not send your questions to the publisher. " +
            "Selkie does not read messages from your other apps, photos or screen content. The policy must identify the actual data controller and provide a way to contact them: here, " +
            "${PrivacyIdentity.controllerName}, ${PrivacyIdentity.contactEmail}. It describes this version; opening accounts or payments will require an update."
    ),
    englishHelp(
        SelkieHelpSection.PRIVACY, "delete_data", "How do I delete my data?",
        listOf("delete my data", "delete all my data", "erase my data", "erase all my data", "data deletion", "delete everything", "start from zero", "delete my account", "erase my account"),
        "In the ☰ menu → Privacy and data → Delete my data, confirm to stop tracking and delete settings, points, challenges, local choices and saved Coach questions. " +
            "This is irreversible; points return to their initial value the next time you use the app. Android statistics and permissions are unchanged. " +
            "No active server account is deleted here, and this deletion would not cancel a future Google Play subscription."
    ),
    englishHelp(
        SelkieHelpSection.PRIVACY, "contact", "How do I contact Selkie?",
        listOf("contact", "contact us", "email", "e mail", "support", "assistance", "write to the developer", "email address", "report a problem"),
        "For a question or problem, write to ${PrivacyIdentity.contactEmail}. The Contact by email button is in the ☰ menu → Privacy and data. " +
            "Explain the problem and, if useful, include your Selkie version and phone model. Avoid sending passwords, banking details or screenshots containing private information. " +
            "Coach phrases are not automatically sent to this address."
    )
)

private fun englishHelp(
    section: SelkieHelpSection,
    id: String,
    label: String,
    keywords: List<String>,
    answer: String
): SelkieHelpEntry = SelkieHelpEntry(section, CoachTopic(
    id = id, label = label, keywords = keywords, replies = listOf(answer), appHelp = true
))
