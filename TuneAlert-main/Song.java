import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Song — ADT แทน "เพลง" หนึ่งเพลง
 *
 * ⚠️ โค้ดตั้งต้นนี้ "ใช้งานได้" แต่มีบั๊กแบบเดียวกับกรณีศึกษาในสไลด์:
 *    rep exposure ทั้งขาเข้าและขาออก, producer ที่แอบ mutate ตัวเอง,
 *    ไม่ validate input และยังไม่ override equals/hashCode
 *
 * ภารกิจของคุณ: ทำให้ Song เป็น immutable class ที่ถูกต้อง "ครบสูตร 6 ข้อ"
 * และ override equals()/hashCode() ตามสัญญาของ Java (ดูรายละเอียดใน README.md)
 */
public final class Song {

    private final String title;
    private final String artist;
    private final List<String> tags;

    /**
     * สร้างเพลงใหม่
     * @param title ชื่่อเพลง ต้องไม่เป็น null และต้องไม่เป็นสตริงว่าง
     * 
     * @param artist ชื่อศิลปิน ต้องไม่เป็น null และต้องไม่เป็นสตริงว่าง
     * 
     * @param tags tags ที่เพลงนั้นติดเข้าไป
     * 
     * @throws IllegalArgumentException
    */

    public Song(String title, String artist, List<String> tags) {
        if( title == null || title.isBlank())
            throw new IllegalArgumentException("");
        
        if( artist == null || title.isBlank())
            throw new IllegalArgumentException("");
        
        if( tags == null || title.isBlank())
            throw new IllegalArgumentException("");
        
        for(String t : tags)
            if(t == null|| title.isBlank())
            throw new IllegalArgumentException(""); 
            
        // TODO(1.1): validate input — title/artist ห้าม null/ว่าง,
        //            tags ห้าม null และห้ามมีสมาชิกเป็น null/ว่าง
        //            ผิดเงื่อนไขให้ throw IllegalArgumentException
        this.title = title;
        this.artist = artist;
        // TODO(1.2): ✗ เก็บลูกศรตรง ๆ = rep exposure ขาเข้า → defensive copy!
        this.tags = new  ArrayList<>(tags);
        checkRep();
    }
    private  void checkRep(){
        assert title!=null && title !="";
        assert artist!=null && artist !="";
        assert tags !=null;
       
    }

    // ---------- observers ----------

    public String title() {
        return title;
    }

    public String artist() {
        return artist;
    }

    public List<String> tags() {
        // TODO(1.3): ✗ ส่งลูกศรออกไปตรง ๆ = rep exposure ขาออก → คืน "สำเนา"
        
        
        return new ArrayList<>(tags);
    }

    // ---------- producer ----------

    /**
     * spec: คืน Song "ตัวใหม่" ที่มีแท็กเพิ่มต่อท้าย — ห้ามแก้ตัวเดิม
     * @throws IllegalArgumentException เมื่อ tag เป็น null/ว่าง
     */
    public Song withTag(String tag) {
        // TODO(1.4): ✗ โค้ดนี้ mutate ตัวเอง! ต้องสร้างและคืน Song ตัวใหม่แทน
        //            (อย่าลืม validate tag ด้วย)
         if(tag == null || tag =="")
            throw new IllegalArgumentException();
        
        List<String> newTags = new ArrayList<>(tags);
        newTags.add(tag);
        return new Song(title,artist,newTags);
    }

    // ---------- equality ----------

    // TODO(1.5): override equals(Object o) แบบ structural equality
    //            เทียบ title, artist และ tags ทีละ field
    //            ตามลำดับมาตรฐาน: ตัวเอง → ชนิด (instanceof) → cast → เทียบ field
    //            ระวัง: ต้องรับ Object ไม่ใช่ Song ไม่งั้นเป็น overload ไม่ใช่ override!
    public boolean equals(Object o){
        if(this == o ) return true;
        if(!(o instanceof Song))
        return false;
        
        Song p = (Song) o;
        return title.equals(p.title) && artist.equals(p.artist) && tags.equals(p.tags);
    }
    



    // TODO(1.6): override hashCode() ให้สอดคล้องกับ equals
    //            (คำนวณจาก field ชุดเดียวกัน — Objects.hash(...) ช่วยได้)
public int hashCode(){
    return Objects.hash(title,artist,tags);
}
    @Override
    public String toString() {
        return title + " — " + artist + " " + tags;
    }
}
