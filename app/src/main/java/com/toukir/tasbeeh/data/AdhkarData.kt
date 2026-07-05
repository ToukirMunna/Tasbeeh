package com.toukir.tasbeeh.data

import android.content.Context
import com.toukir.tasbeeh.R

data class AdhkarInfo(
    val name: String,
    val translation: String,
    val virtue: String,
    val arabic: String = "" 
)

data class LibraryItem(
    val keyName: String, // The English name acting as key
    val nameResId: Int,
    val transResId: Int,
    val virtueResId: Int,
    val arabic: String
)

object AdhkarLibrary {
    // Keep the internal list with Resource IDs
    private val libraryItems = listOf(
        LibraryItem("SubhanAllah", R.string.adhkar_subhanallah, R.string.adhkar_subhanallah_trans, R.string.adhkar_subhanallah_virtue, "سبحان الله"),
        LibraryItem("Alhamdulillah", R.string.adhkar_alhamdulillah, R.string.adhkar_alhamdulillah_trans, R.string.adhkar_alhamdulillah_virtue, "الحمد لله"),
        LibraryItem("Allahu Akbar", R.string.adhkar_allahuakbar, R.string.adhkar_allahuakbar_trans, R.string.adhkar_allahuakbar_virtue, "الله أكبر"),
        LibraryItem("La Ilaha Illallah", R.string.adhkar_lailahaillallah, R.string.adhkar_lailahaillallah_trans, R.string.adhkar_lailahaillallah_virtue, "لا إله إلا الله"),
        LibraryItem("Astaghfirullah", R.string.adhkar_astaghfirullah, R.string.adhkar_astaghfirullah_trans, R.string.adhkar_astaghfirullah_virtue, "أستغفر الله"),
        LibraryItem("SubhanAllahi wa bihamdihi", R.string.adhkar_subhanallah_bihamdihi, R.string.adhkar_subhanallah_bihamdihi_trans, R.string.adhkar_subhanallah_bihamdihi_virtue, "سبحان الله وبحمده"),
        LibraryItem("SubhanAllahil Azeem", R.string.adhkar_subhanallah_azeem, R.string.adhkar_subhanallah_azeem_trans, R.string.adhkar_subhanallah_azeem_virtue, "سبحان الله العظيم"),
        LibraryItem("La hawla wa la quwwata illa billah", R.string.adhkar_lahawla, R.string.adhkar_lahawla_trans, R.string.adhkar_lahawla_virtue, "لا حول ولا قوة إلا بالله"),
        LibraryItem("The Four Phrases", R.string.adhkar_four_phrases, R.string.adhkar_four_phrases_trans, R.string.adhkar_four_phrases_virtue, "سبحان الله والحمد لله ولا إله إلا الله والله أكبر"),
        LibraryItem("HasbunAllahu wa ni'mal wakil", R.string.adhkar_hasbunallah, R.string.adhkar_hasbunallah_trans, R.string.adhkar_hasbunallah_virtue, "حسبنا الله ونعم الوكيل"),
        LibraryItem("Ayatul Kursi", R.string.adhkar_ayatul, R.string.adhkar_ayatul_trans, R.string.adhkar_ayatul_virtue, "الله لا إله إلا هو الحي القيوم..."),
        LibraryItem("Subhanal Malikil Quddus", R.string.adhkar_malikil_quddus, R.string.adhkar_malikil_quddus_trans, R.string.adhkar_malikil_quddus_virtue, "سُبْحَانَ الْمَلِكِ الْقُدُّوسِ"),
        LibraryItem("Ya Dhul Jalali wal Ikram", R.string.adhkar_dhul_jalali, R.string.adhkar_dhul_jalali_trans, R.string.adhkar_dhul_jalali_virtue, "يَا ذَا الْجَلَالِ وَالْإِكْرَامِ"),
        LibraryItem("Allahumma Ajirni minan Nar", R.string.adhkar_ajirni_nar, R.string.adhkar_ajirni_nar_trans, R.string.adhkar_ajirni_nar_virtue, "اللَّهُمَّ أَجِرْنِي مِنَ النَّارِ"),
        LibraryItem("Allahumma inni as-alukal Jannah", R.string.adhkar_as_alukal_jannah, R.string.adhkar_as_alukal_jannah_trans, R.string.adhkar_as_alukal_jannah_virtue, "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْجَنَّةَ"),
        LibraryItem("Rabbighfir li", R.string.adhkar_rabbighfir_li, R.string.adhkar_rabbighfir_li_trans, R.string.adhkar_rabbighfir_li_virtue, "رَبِّ اغْفِرْ لِي")
    )

    // Backward compatibility for existing code that expects a list of AdhkarInfo
    // Note: This returns English versions by default if context is not available?
    // Actually, we can't get strings without context. 
    // We will keep 'adhkarList' as a list of "Default English" AdhkarInfo for Repository usage (initial DB population)
    // But ideally we should use the keys.
    
    val adhkarList: List<AdhkarInfo>
        get() = libraryItems.map { 
            AdhkarInfo(it.keyName, "", "", it.arabic) // Values are empty or placeholders, mainly keyName matters for ID
        }

    fun getInfo(name: String): AdhkarInfo? {
        val item = libraryItems.find { 
             it.keyName.equals(name, ignoreCase = true) || 
             name.contains(it.keyName, ignoreCase = true) 
        } ?: return null
        
        // Return with empty strings? This function was used to get details.
        // We need Context to return real strings. 
        // We will overload this function.
        return AdhkarInfo(item.keyName, "", "", item.arabic)
    }

    fun getLocalizedInfo(context: Context, name: String): AdhkarInfo? {
         val item = libraryItems.find { 
             it.keyName.equals(name, ignoreCase = true) || 
             name.contains(it.keyName, ignoreCase = true) // Fuzzy match for "The Four Phrases" etc if stored differently
        } ?: return null
        
        return AdhkarInfo(
            name = context.getString(item.nameResId),
            translation = context.getString(item.transResId),
            virtue = context.getString(item.virtueResId),
            arabic = item.arabic
        )
    }
    
    fun getLocalizedName(context: Context, name: String): String {
        val item = libraryItems.find { it.keyName.equals(name, ignoreCase = true) }
        return if (item != null) context.getString(item.nameResId) else name
    }
    
    // Helper to get full list populated with localized strings
    fun getLocalizedList(context: Context): List<AdhkarInfo> {
        return libraryItems.map {
            AdhkarInfo(
                name = context.getString(it.nameResId),
                translation = context.getString(it.transResId),
                virtue = context.getString(it.virtueResId),
                arabic = it.arabic
            )
        }
    }
    
    // Helper to match localized name back to key (optional, for reverse lookup if needed)
    // But we mostly store the English key in DB.
}
