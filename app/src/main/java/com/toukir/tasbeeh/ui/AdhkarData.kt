package com.toukir.tasbeeh.ui

data class AdhkarInfo(
    val name: String,
    val translation: String,
    val virtue: String,
    val arabic: String = "" // Optional if you want to display Arabic text separately
)

object AdhkarLibrary {
    val adhkarList = listOf(
        AdhkarInfo(
            name = "SubhanAllah",
            translation = "Glory be to Allah",
            virtue = "A tree is planted for the reciter in Paradise.",
            arabic = "سبحان الله"
        ),
        AdhkarInfo(
            name = "Alhamdulillah",
            translation = "All praise is due to Allah",
            virtue = "It fills the scales of good deeds.",
            arabic = "الحمد لله"
        ),
        AdhkarInfo(
            name = "Allahu Akbar",
            translation = "Allah is the Greatest",
            virtue = "Fills the space between the heavens and the earth with rewards.",
            arabic = "الله أكبر"
        ),
        AdhkarInfo(
            name = "La Ilaha Illallah",
            translation = "There is no god but Allah",
            virtue = "The best form of remembrance (Dhikr).",
            arabic = "لا إله إلا الله"
        ),
        AdhkarInfo(
            name = "Astaghfirullah",
            translation = "I seek forgiveness from Allah",
            virtue = "Removes anxiety and opens doors to provision.",
            arabic = "أستغفر الله"
        ),
        AdhkarInfo(
            name = "SubhanAllahi wa bihamdihi",
            translation = "Glory be to Allah and His is the praise",
            virtue = "Whoever says this 100 times a day, their sins are forgiven even if they are as much as the foam of the sea.",
            arabic = "سبحان الله وبحمده"
        ),
        AdhkarInfo(
            name = "SubhanAllahil Azeem",
            translation = "Glory be to Allah, the Magnificent",
            virtue = "One of the two phrases that are light on the tongue but heavy on the scale.",
            arabic = "سبحان الله العظيم"
        ),
        AdhkarInfo(
            name = "La hawla wa la quwwata illa billah",
            translation = "There is no power and no strength except with Allah",
            virtue = "A treasure from the treasures of Paradise.",
            arabic = "لا حول ولا قوة إلا بالله"
        ),
        AdhkarInfo(
            name = "The Four Phrases",
            translation = "SubhanAllah, wal-hamdulillah, wa la ilaha illallah, wallahu akbar",
            virtue = "The most beloved words to Allah.",
            arabic = "سبحان الله والحمد لله ولا إله إلا الله والله أكبر"
        ),
        AdhkarInfo(
            name = "Durood (Salawat)",
            translation = "O Allah, send blessings upon Muhammad",
            virtue = "Allah sends 10 blessings upon the one who sends 1 blessing upon the Prophet (SAW).",
            arabic = "اللهم صل على محمد"
        ),
        AdhkarInfo(
            name = "HasbunAllahu wa ni'mal wakil",
            translation = "Sufficient for us is Allah, and [He is] the best Disposer of affairs",
            virtue = "Protection against fear and harm.",
            arabic = "حسبنا الله ونعم الوكيل"
        ),
        AdhkarInfo(
            name = "La ilaha illallah wahdahu...",
            translation = "There is no god but Allah alone, He has no partner. His is the dominion, and His is the praise, and He is capable of all things.",
            virtue = "Reciting 100 times is like freeing 10 slaves, and provides protection from Shaytan.",
            arabic = "لا إله إلا الله وحده لا شريك له، له الملك وله الحمد، وهو على كل شيء قدير"
        ),
        AdhkarInfo(
            name = "Radhitu billahi Rabba",
            translation = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad as my Prophet",
            virtue = "Paradise becomes mandatory for the one who says this.",
            arabic = "رضيت بالله رباً وبالإسلام ديناً وبمحمد نبياً"
        ),
        AdhkarInfo(
            name = "Ya Hayyu Ya Qayyum",
            translation = "O Living, O Sustaining, in Your Mercy I seek relief",
            virtue = "A supplication the Prophet (SAW) used to make during times of distress.",
            arabic = "يا حي يا قيوم برحمتك أستغيث"
        ),
        AdhkarInfo(
            name = "Laylatul Qadr Dua",
            translation = "O Allah, You are Forgiving and love forgiveness, so forgive me",
            virtue = "The recommended Dua for Laylatul Qadr (Night of Decree).",
            arabic = "اللهم إنك عفو تحب العفو فاعف عني"
        ),
        AdhkarInfo(
            name = "Rabbana Atina",
            translation = "Our Lord, give us in this world good and in the Hereafter good and protect us from the punishment of the Fire",
            virtue = "A comprehensive Dua for success in both worlds.",
            arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ"
        ),
        AdhkarInfo(
            name = "Istighfar (100x)",
            translation = "My Lord, forgive me and accept my repentance; indeed You are the Accepting of repentance, the Merciful",
            virtue = "The Prophet (SAW) used to say this 100 times in a single sitting.",
            arabic = "رَبِّ اغْفِرْ لِي وَتُبْ عَلَيَّ إِنَّكَ أَنْتَ التَّوَّابُ الرَّحِيمُ"
        ),
        AdhkarInfo(
            name = "Protection Dua",
            translation = "In the name of Allah, with whose name nothing on earth or in the heaven can cause harm...",
            virtue = "Protection from sudden affliction if said 3 times morning and evening.",
            arabic = "بسم الله الذي لا يضر مع اسمه شيء..."
        ),
        AdhkarInfo(
            name = "Ayatul Kursi",
            translation = "Verse of the Throne (2:255)",
            virtue = "Greatest verse in the Quran; protection from Shaytan.",
            arabic = "الله لا إله إلا هو الحي القيوم..."
        ),
        AdhkarInfo(
            name = "Surah Al-Ikhlas",
            translation = "Say, He is Allah, One...",
            virtue = "Equivalent to one-third of the Quran.",
            arabic = "قُلْ هُوَ اللَّهُ أَحَدٌ"
        )
    )

    fun getInfo(name: String): AdhkarInfo? {
        // Simple fuzzy match or exact match
        return adhkarList.find { 
            it.name.equals(name, ignoreCase = true) || 
            name.contains(it.name, ignoreCase = true) 
        }
    }
}
