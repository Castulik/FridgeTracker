# 🧊 FridgeTracker

**FridgeTracker** je Android aplikace pro správu skladování potravin v lednici a mrazáku. Aplikace pomáhá hlídat trvanlivost potravin, upozorňuje na blížící se expiraci a umožňuje snadné sdílení skladu mezi více uživateli.

## 📱 Hlavní funkce

- 🏠 **Přidání skladu** – vytvoření nového skladu s názvem a popisem.
- ✏️ **Úprava skladu** – změna názvu, ikony a pořadí skladu.
- 🥫 **Ruční přidání potraviny** – zadání názvu, kategorie, množství, hmotnosti, čárového kódu, fotky, expirace a poznámky.
- 📷 **Přidání pomocí čárového kódu** – skenování kódu s automatickým doplněním údajů.
- 🔄 **Úprava potraviny** – možnost zpětně upravit uložené informace.
- ⏰ **Upozornění na expiraci** – automatické notifikace při blížící se spotřebě.
- 🛒 **Nákupní seznam** – přesouvání potravin mezi seznamem a skladem.
- 🔍 **Vyhledávání potravin** – podle názvu nebo čárového kódu napříč sklady.
- 📦 **Správa potravin** – kopírování, přesouvání nebo mazání potravin.
- 📊 **Vizualizace doby trvanlivosti** – indikátor zbývajících dní do expirace.
- 🔃 **Řazení potravin** – podle data, názvu nebo množství.
- 🎴 **Změna vzhledu kartičky** – detailní nebo kompaktní režim.
- 📅 **Nastavení notifikací** – volba počtu dní před expirací.
- 🤖 **AI prompt builder** – generování textu např. pro recepty podle obsahu skladu.
- 👥 **Sdílení skladu** – pozvání dalších uživatelů ke spolupráci.

## ⚙️ Nefunkční požadavky

- 📱 **Podpora zařízení** – Aplikace je plně funkční na Android 8.0 (API 26) a vyšším.
- 🌍 **Lokalizace** – Podpora českého a anglického jazyka, jazyk se přizpůsobuje systému.
- 🔐 **Bezpečnost dat** – Uživatelská data jsou uložena lokálně a nejsou sdílena s třetími stranami.
- 🧑‍💻 **Uživatelská přívětivost** – Rozhraní je přehledné a snadno použitelné i pro začátečníky.
- 📴 **Offline režim** – Aplikaci lze používat bez připojení k internetu, včetně přidávání a úprav potravin.
- ⚡ **Výkonnost a stabilita** – Aplikace funguje plynule i na běžných zařízeních bez výrazného zpomalování nebo pádů.


## 🖼️ Ukázky uživatelského rozhraní

![Domácí obrazovka](screenshots/home.png)
![Detail potraviny](screenshots/item_detail.png)
![Nákupní seznam](screenshots/shopping_list.png)

## 🏗️ Technologie

- 🧠 Jetpack Compose (UI)
- 🗃️ Room (lokální databáze)
- 📦 MVVM architektura
- 🔔 Notifikace
- 📸 Podpora fotek a skenování čárových kódů
- 🌐 Lokalizace CZ / EN

## 🚧 Vývoj

Aplikace je ve fázi vývoje, některé funkce mohou být nedokončené nebo v testovací verzi. Feedback je vítán! ✨

## 🛡️ Licence

Tento projekt slouží pro demonstrační a vzdělávací účely. Je dostupný pod [MIT licencí](LICENSE).
