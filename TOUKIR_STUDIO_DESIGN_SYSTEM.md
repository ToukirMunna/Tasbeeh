# 📐 Toukir Studio Unified Design System (TDS)
**The Architectural & Visual Specification for the Toukir Studio App Fleet**  
*Synthesized from:* `PhoneUsageHistory`, `LexiCore`, `Audia`, and the `designphilosophy.md` Manifesto  
*Location:* `C:\Projects\Docs\TOUKIR_STUDIO_DESIGN_SYSTEM.md`  

---

## 1. 🏛️ The Complete Toukir Studio Color Spectrum

We strictly eliminate dynamic wallpaper tinting (`dynamicColor = false`) and lock in **strictly two themes (Light and Dark)** with guaranteed WCAG AAA contrast.

### A. The Structural Canvas & Typography (The 90% Neutral Workhorses)

No pure AMOLED black (`#000000`) and no pure blinding white canvas backgrounds. Depth is created via surface luminance steps and crisp `1.dp` hairline rims.

| Role | Light Theme Hex | Dark Theme Hex | Contrast Ratio |
|---|---|---|:---:|
| **Canvas (Background)** | `#F0F2F5` (Cool Slate) | `#111317` (Deep Obsidian Slate) | — |
| **Card (Surface)** | `#FFFFFF` (Pure Luminous) | `#1A1D23` (Elevated Charcoal) | — |
| **Inset (Tracks/Bars)** | `#E4E8EE` (Recessed Slate) | `#232730` (Recessed Dark) | — |
| **Hairline Border** | `#D2D8E2` (1dp Tactile Rim) | `#2D323E` (1dp Tactile Rim) | — |
| **Text Primary** | `#111827` (Deep Ink) | `#F3F4F6` (Luminous White) | **16.5 : 1** (AAA) |
| **Text Secondary** | `#4B5563` (Muted Slate) | `#9CA3AF` (Muted Silver) | **5.8 : 1** (AA) |

---

### B. The 5 Studio Brand Accents (The 10% Surgical Pop & Icon Colors)

Every app belongs to a primary brand category that dictates its **Icon Glyph Color** and its **in-app 10% surgical accent**:

```
╔═════════════════════════════════════════════════════════════════════════════╗
║ 1. ⚡ TOUKIR COBALT (Primary Brand Signature)                              ║
║    Light: #2563EB  |  Dark: #3B82F6  |  Pill Container: #DBEAFE / #1E3A8A   ║
║    Domain: System, Storage, Security & Core Tools                           ║
║    Apps: Everything, Encrypta, PhoneUsage, PulseDeck, WirelessDebug         ║
╠═════════════════════════════════════════════════════════════════════════════╣
║ 2. 🌿 TOUKIR MINT (Health, Growth, Habits & Spiritual Rituals)              ║
║    Light: #0D9488  |  Dark: #14B8A6  |  Pill Container: #CCFBF1 / #134E4A   ║
║    Domain: Meditation, Hydration, Daily Dhikr & Biological Cycles           ║
║    Apps: Tasbeeh, DrinkWater, Curio, Rhythm                                 ║
╠═════════════════════════════════════════════════════════════════════════════╣
║ 3. 🎙️ TOUKIR AMBER (Acoustic Sound, Energy, Time & Focus)                   ║
║    Light: #D97706  |  Dark: #F59E0B  |  Pill Container: #FEF3C7 / #451A03   ║
║    Domain: Audio Waveforms, Voice Typing, Emergency Priorities, Screen-Time ║
║    Apps: Audia, VoiceInput, Equinox, KidoPlayer                             ║
╠════════════════════════════════════════════════════════════════════════════╣
║ 4. 🔮 TOUKIR VIOLET (Knowledge, Thought & Literary Space)                  ║
║    Light: #6D28D9  |  Dark: #8B5CF6  |  Pill Container: #EDE9FE / #2E1065   ║
║    Domain: Spaced Repetition, Intimate Journaling, E-Reading, Bookshelves   ║
║    Apps: LexiCore, lumina, MyDiary, Narra                                   ║
╠════════════════════════════════════════════════════════════════════════════╣
║ 5. 💎 TOUKIR EMERALD (Commerce, Finance & Matrimonial Records)              ║
║    Light: #059669  |  Dark: #10B981  |  Pill Container: #D1FAE5 / #064E3B   ║
║    Domain: Retail Ledgers, Transaction Tickets, Family Budgets              ║
║    Apps: ExpenseTracker, EasyPOS, MarriageIndex                             ║
╠════════════════════════════════════════════════════════════════════════════╣
║ 🛑 ALERT EXCEPTION: TOUKIR CRIMSON (Destructive Actions / Live Mic)        ║
║    Light: #DC2626  |  Dark: #EF4444  |  Pill Container: #FEE2E2 / #450A0A   ║
╚═════════════════════════════════════════════════════════════════════════════╝
```

---

## 2. 🎨 The Hardware-Badge Icon Specification

To avoid the flat, dead look of Google's themed icons, every Toukir Studio icon is constructed as an **illuminated hardware badge**:

```
 ┌─────────────────────────────────────────────────────────┐
 │ BACKGROUND LAYER (108dp canvas):                        │
 │ • Deep Obsidian Slate Tile (#13151A)                    │
 │ • Subtle 1.dp hairline perimeter rim (#2A2E39)          │
 │ • Optical top-to-bottom fine gradient (+4% luminance)   │
 ├─────────────────────────────────────────────────────────┤
 │ FOREGROUND GLYPH (72dp safe area):                      │
 │ • Sculpted geometric vector silhouette                  │
 │ • Colored with the app's dedicated Category Accent      │
 │   (Cobalt, Mint, Amber, Violet, or Emerald)             │
 │ • Micro-specular accent or dual-tone highlight          │
 └─────────────────────────────────────────────────────────┘
```

---

## 3. 🏛️ Card & Component Rules

1. **Card Geometry**:
   * Hero Cards: `20.dp` rounded corners.
   * Standard List Items: `16.dp` rounded corners.
   * Border: Always `BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)`.
2. **Button Step-Down**:
   * Primary Buttons: `12.dp` rounded corners, `48.dp` height, `FontWeight.SemiBold`.
   * Floating Actions / Docks: `CircleShape` or `50%` Pill.
3. **Tactile Haptics**:
   * Standard click: `TextHandleMove` (crisp click).
   * Confirmation / Save: `LongPress` (firm pulse).
4. **Motion**:
   * Spring curves with `damping = 0.8f`, `stiffness = MediumLow`.

---

## 4. 🚀 Universal Studio Signature

Every app incorporates the **Toukir Studio Universal About Sheet**:
* Accessible from top-right toolbar or Settings.
* Toukir Studio brand monogram.
* Canonical package: `com.toukirstudio.<appname>`.
* Privacy pledge: *"Zero Ads. Zero Telemetry. Crafted by Toukir Studio."*
* Horizontal carousel featuring sibling apps in the ecosystem.
