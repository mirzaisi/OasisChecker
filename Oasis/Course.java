package com.oasischecker.Oasis;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Course implements Serializable{
    private String courseCode;
    private String courseName;
    private String letterGrade;
    private ArrayList<String> courseGrades;

    final static String serializedFilePathRoot = "oasis-checker\\src\\main\\java\\com\\oasischecker\\Serialized\\"; //TODO put a dir path

    public Course(String courseCode, String courseName, String letterGrade, ArrayList<String> courseGrades) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.letterGrade = letterGrade;
        this.courseGrades = courseGrades;
    }

    public String getFormattedCourseCode() {
        return this.getCourseCode().replaceAll("-", "_").replaceAll("\s+", "");
    }
    
    public String getCourseCode() {
        return courseCode;
    }


    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }


    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }


    public String getLetterGrade() {
        return letterGrade;
    }


    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }


    public ArrayList<String> getCourseGrades() {
        return courseGrades;
    }


    public void setCourseGrades(ArrayList<String> courseGrades) {
        this.courseGrades = courseGrades;
    }

    private static ArrayList<String> compareCourses(ArrayList<Course> existingCourses, ArrayList<Course> newCourses) {
        System.out.println("Comparing old and new course objects...");
        ArrayList<String> courseCodesToAddToMail = new ArrayList<>();

        for (Course newCourse : newCourses) {
            for (Course existingCourse : existingCourses) {    
                if (existingCourse.getCourseCode().equals(newCourse.getCourseCode())) {
                    if(Course.isChangedCourseGrade(existingCourse.getCourseGrades(), newCourse.getCourseGrades()) || Course.isChangedCourseLetterGrade(existingCourse.getLetterGrade(), newCourse.getLetterGrade())) {
                        System.out.println("Change in course " + newCourse.getCourseCode() + 
                        "/" + newCourse.getCourseName() + " detected. Appended it to email content.");
                        courseCodesToAddToMail.add(newCourse.getCourseCode());
                    }
                }
            }

            // if there is a grade section but all grades are empty, don't send it
            if(newCourse.isEmptyCourseGrades()) {;
                courseCodesToAddToMail.remove(newCourse.getCourseCode());
            }
            
            newCourse.serialize();
        }

       
        return courseCodesToAddToMail;
    }

    public boolean isEmptyCourseGrades() {
        for (String grade : this.getCourseGrades()) {
            if (!grade.equals("")) {
                return false;
            }
        }

        return true;
        
    }

    public static boolean isChangedCourseLetterGrade(String existingCourseLetterGrade, String newCourseLetterGrade) {
        boolean isChangedCourseLetterGrade = false;
        if (!existingCourseLetterGrade.equals(newCourseLetterGrade)) {
            isChangedCourseLetterGrade = true;
        }

        return isChangedCourseLetterGrade;
    }
    public static boolean isChangedCourseGrade(ArrayList<String> existingCourseGrades, ArrayList<String> newCourseGrades) {
        boolean isChangedCourseGrade = false;
        if (existingCourseGrades.size() != newCourseGrades.size()) {
            isChangedCourseGrade = true;
        } else {
            for (int i = 0 ; i < existingCourseGrades.size() ; i++) {
                if (!existingCourseGrades.get(i).equals(newCourseGrades.get(i))) {
                    isChangedCourseGrade = true;
                }
            }
        }  

        return isChangedCourseGrade;
    }


     public static ArrayList<String> loadCourseObjects(List<WebElement> courses) {
        System.out.println("Deserializing old course objects...");
        ArrayList<String> courseCodesToAddToMail = null;
        ArrayList<Course> existingCourses = Course.deserialize();

        ArrayList<Course> newCourses = createCourseObjects(courses);
                
        courseCodesToAddToMail = compareCourses(existingCourses, newCourses);
        

        return courseCodesToAddToMail;
    }

    private static ArrayList<Course> createCourseObjects(List<WebElement> courses) {
        ArrayList<Course> newCourses = new ArrayList<>();
        for (WebElement course : courses) {
            String courseCode = course.findElement(By.cssSelector("tr.border-bottom-10.green-table > td:nth-child(1)")).getText();
            String courseName = course.findElement(By.cssSelector("tr.border-bottom-10.green-table > td:nth-child(2)")).getText();
            String letterGrade = course.findElement(By.cssSelector("tr.border-bottom-10.green-table > td:nth-child(6) > i")).getText();
            ArrayList<String> gradesAsString = new ArrayList<>();

            Course courseObject = new Course(courseCode, courseName, letterGrade, gradesAsString);
            newCourses.add(courseObject);

            List<WebElement> gradesFromOasis = course.findElements(By.cssSelector("tr:nth-child(2) > td > table > tbody > tr:nth-child(2) > td"));
            for(WebElement grade: gradesFromOasis) {
                gradesAsString.add(grade.getText());
            }
            
        }

        return newCourses;
        
    }

    public boolean serialize() {
        String serializedFilePath = serializedFilePathRoot + this.getFormattedCourseCode() + ".ser";
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(serializedFilePath))) {
            objectOutputStream.writeObject(this);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<Course> deserialize() {
        ArrayList<Course> deserializedCourses = new ArrayList<>();
        File serFileDir = new File(serializedFilePathRoot);
        File[] serFiles = serFileDir.listFiles();
        if (serFiles != null) {
            for(File serFile : serFiles) {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serFile))) {
                    Course deserializedCourse = null;
                    while ((deserializedCourse = (Course) objectInputStream.readObject()) != null) {
                        deserializedCourses.add(deserializedCourse);
                    }
                    
                } catch (EOFException eofe) {
                    continue;
                } 
                
                catch (Exception e) {
                    e.printStackTrace();
                }
    
            }
        }
        
        return deserializedCourses;
    }
}
