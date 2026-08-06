# Lab Week 6 — TuneAlert: ระบบแจ้งเตือนเพลงใหม่

**หัวข้อ:** Mutability, Equality & SOLID · 

## สถานการณ์

สตาร์ทอัพเพลงแห่งหนึ่งกำลังสร้างระบบ **TuneAlert** — เมื่อมีเพลงใหม่ปล่อยออกมา ระบบจะกระจายข้อความแจ้งเตือนไปยังช่องทางต่าง ๆ (อีเมล, SMS, และช่องทางอื่นในอนาคต)

รุ่นพี่ทิ้งโค้ดตั้งต้นไว้ให้ใน `starter/` … แต่โค้ดชุดนี้มีบั๊กครบทุกแบบที่เรียนมาในสัปดาห์นี้: **rep exposure ทั้งขาเข้าขาออก, aliasing, producer ที่แอบ mutate ตัวเอง, ลืม equals/hashCode และออกแบบยังไม่ครบตามหลัก SOLID** — งานของคุณคือแก้ให้ถูกต้องจนผ่านเทสทั้งหมด

## วัตถุประสงค์การเรียนรู้

เมื่อทำแลปนี้เสร็จ คุณจะได้ฝึก:

1. สร้าง **immutable class ครบสูตร 6 ข้อ** (final class · private final · ไม่มี setter · producer · defensive copy ขาเข้า · defensive copy ขาออก)
2. เขียน **equals() และ hashCode()** ให้ทำตามสัญญาของ Java (reflexive, symmetric, transitive, consistent, non-null) และใช้งานร่วมกับ `HashSet` ได้จริง
3. ใช้ **enum ที่มี field/constructor/method** แทน int constants
4. ใช้ **composition + delegation (Decorator)** แทน inheritance
5. ประยุกต์ **SOLID**: SRP, OCP (เพิ่มช่องทางใหม่โดยไม่แก้ของเดิม), ISP (interface เล็ก), DIP (ฉีด dependency ผ่าน constructor) และ **LSP** (ไม่มี method ไหนต้องขว้าง `UnsupportedOperationException`)
6. ป้องกัน **aliasing bug** ด้วย defensive copy

## โครงสร้างไฟล์

```
starter/
├── Song.java                 ← งานที่ 1 (มีบั๊ก ให้แก้)
├── Priority.java             ← งานที่ 2 (โครงว่าง ให้เติม)
├── CountingNotifier.java     ← งานที่ 3 (โครงว่าง ให้เติม)
├── NotificationService.java  ← งานที่ 4 (มีบั๊ก ให้แก้)
├── Notifier.java             ← ให้มาแล้ว ห้ามแก้
├── EmailNotifier.java        ← ให้มาแล้ว ห้ามแก้
├── SmsNotifier.java          ← ให้มาแล้ว ห้ามแก้
└── TestRunner.java           ← ชุดเทส ห้ามแก้
```

ไล่ตาม `TODO(x.y)` ในแต่ละไฟล์ได้เลย — แนะนำให้ทำเรียงตามลำดับภารกิจ

---

## งานที่ 1 — ทำให้ `Song` เป็น Immutable ADT ที่ถูกต้อง

`Song` แทนเพลงหนึ่งเพลง มี `title`, `artist` และรายการแท็ก `tags` โค้ดตั้งต้นมีบั๊กแบบเดียวกับกรณีศึกษา `Student` ในสไลด์ ให้แก้จนครบสูตร immutable 6 ข้อ

**Spec ที่ต้องทำให้ได้:**

| สมาชิก | สัญญา (spec) |
|---|---|
| `Song(title, artist, tags)` | สร้างเพลงใหม่ · `title`/`artist` ห้าม null/ว่าง · `tags` ห้าม null และห้ามมีสมาชิก null/ว่าง · ผิดเงื่อนไข → `IllegalArgumentException` · **ต้อง defensive copy `tags` ขาเข้า** |
| `title()`, `artist()` | observer คืนค่า (String เป็น immutable อยู่แล้ว คืนตรง ๆ ได้) |
| `tags()` | observer **คืนสำเนา** ของรายการแท็ก — คนนอกแก้ list ที่ได้ไปต้องไม่กระทบเพลง และเรียกสองครั้งต้องได้คนละ object |
| `withTag(tag)` | **producer** คืน `Song` ตัวใหม่ที่มีแท็กเพิ่มต่อท้าย ตัวเดิมห้ามเปลี่ยน · `tag` null/ว่าง → `IllegalArgumentException` |
| `equals(Object o)` | **structural equality** — เท่ากันเมื่อ `title`, `artist`, `tags` ตรงกันทุก field · ทำตามสัญญา 5 ข้อ · `equals(null)` คืน false ห้าม throw |
| `hashCode()` | สอดคล้องกับ equals: เท่ากันเมื่อไร hash ต้องเท่ากัน (แนะนำ `Objects.hash(...)`) |

> คิดก่อนเขียน: ทำไม `Song` ซึ่งเป็น immutable ถึง "ควร" override equals เทียบค่า ในขณะที่ `ArrayList` ซึ่งเป็น mutable ไม่ควรเอาไปเป็นสมาชิก `HashSet`? 

## งานที่ 2 — เติม enum `Priority`

ระดับความสำคัญของข้อความมี 3 ระดับ ให้เพิ่ม field ตามแบบ `enum Planet` ในสไลด์:

| ค่า | level |
|---|---|
| `LOW` | 1 |
| `NORMAL` | 2 |
| `URGENT` | 3 |

- `level()` — คืนตัวเลขระดับ
- `isAtLeast(Priority other)` — คืน `true` เมื่อระดับของเรา ≥ ระดับของ `other` (ระดับเท่ากันนับว่า "ถึงเกณฑ์")

## งานที่ 3 — เขียน `CountingNotifier` ด้วย Composition

ทีมอยากรู้สถิติว่าแต่ละช่องทางส่งข้อความไปกี่ครั้ง **ห้ามใช้ inheritance** (นึกถึงบั๊ก `CountingSet extends HashSet` ในสไลด์ 30) — ให้เขียนเป็น **wrapper (Decorator)** ที่:

- `implements Notifier` และ **ถือ** `Notifier inner` ไว้ข้างใน (has-a) โดยรู้จักผ่าน interface เท่านั้น
- `CountingNotifier(inner)` — `inner` เป็น null → `IllegalArgumentException`
- `send(message)` — นับหนึ่งครั้ง แล้ว **มอบงาน** ให้ `inner.send(message)`
- `sendCount()` — คืนจำนวนครั้งที่ถูกเรียก `send`

ผลพลอยได้ของ composition: ห่อได้ทั้ง `EmailNotifier`, `SmsNotifier` หรือแม้แต่ `CountingNotifier` ซ้อนกันเอง

## งานที่ 4 — แก้ `NotificationService` ให้ปลอดภัยและครบ SOLID

โครง DIP ทำมาให้ครึ่งทางแล้ว (รับ `List<Notifier>` ฉีดผ่าน constructor — ไม่มี `new EmailNotifier()` ข้างใน) ให้แก้ส่วนที่เหลือ:

- **Constructor:** validate (`channels` ห้าม null/มีสมาชิก null, `threshold` ห้าม null → `IllegalArgumentException`) และ **defensive copy** list ที่รับมา — ผู้เรียกแก้ list ต้นทางภายหลังต้องไม่กระทบ service
- **`broadcast(message, priority)`:** validate (`message` ห้าม null/ว่าง, `priority` ห้าม null) · ถ้า `priority` ต่ำกว่า `threshold` ให้**ไม่ส่ง**และคืน `false` · ถึงเกณฑ์ (รวมเท่าเกณฑ์พอดี) ให้ส่งครบทุกช่องทางแล้วคืน `true`
- `channelCount()` และ `announceNewSong(...)` ให้มาแล้ว ไม่ต้องแก้

> สังเกตว่าในชุดเทสมี `FakeNotifier` ปลอมมาเสียบแทนอีเมล/SMS จริง — นี่คือประโยชน์ตรง ๆ ของ DIP: ทดสอบโมดูลระดับสูงได้โดยไม่ต้องต่อ SMTP จริง และการที่เทสเพิ่ม "ช่องทางใหม่" ได้โดยไม่แตะ `NotificationService` เลย ก็คือ OCP ทำงานจริง

---



## กติกา

- ห้ามแก้ `Notifier.java`, `EmailNotifier.java`, `SmsNotifier.java` และ `TestRunner.java`
- ห้ามลบ/เปลี่ยน signature ของ method ที่ spec กำหนด (เพิ่ม private helper ได้)
- ห้ามให้ method ใดขว้าง `UnsupportedOperationException` — ถ้ารู้สึกว่าต้องขว้าง แปลว่าออกแบบผิดหลัก LSP/ISP


