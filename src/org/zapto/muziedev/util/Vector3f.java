package org.zapto.muziedev.util;

/**
 *
 * @author Anton
 */
public class Vector3f {
    private float x,y,z;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
    
    public Vector3f(float x,float y,float z){
        setX(x);
        setY(y);
        setZ(z);
    }
    public Vector3f add(Vector3f x){
        return new Vector3f(this.getX() + x.getX(), this.getY() + x.getY(), this.getZ() + x.getZ());
    }
    public static Vector3f Zero(){
        return new Vector3f(0,0,0);
    }
    public String toString(){
        return "Vector3f(" + getX()+ "," + getY()+","+ getZ()+")";
    }
}
