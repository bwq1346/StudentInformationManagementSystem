/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studentinformationmanagementsystem;

/**
 * The possible states a student can be in. ACTIVE is what most students
 * will be - the rest cover a student on leave, one who's finished their
 * study, or one who's dropped out.
 *
 * @author Nishal Kinattukarayil Biju
 */
public enum StudentStatus {
    ACTIVE,
    SUSPENDED,
    GRADUATED,
    WITHDRAWN
}