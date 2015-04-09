class Machine {
	private  String name;
	private int code;
	
	public Machine() { 
		System.out.println("Contructor Running");
		
		name = "This name";
	}
	
	public Machine(String name){
		System.out.println("Contructor Running 2");
		this.name = name;
	}
	public Machine(String name, int code){
		this.code = code;
		this.name = name;
		System.out.println("Contructor Running 3");
		
	}
	public Machine(int code){
		this.code = code;
		System.out.println("Contructor Running 4");
		
		
	}
	public void name(){
		System.out.println(name);
	}
	
	public String getName(){
		System.out.println(name);
		return name;
	}
	
	public void setName(String newName)
	{
		this.name = newName;
	}
	
	public void setName(int newName)
	{
		this.code = newName;
	}
	
	
}

public class App {
	public static void main(String[] args){
		Machine machine1 = new Machine();
		Machine machine2 = new Machine("Bertie");
		Machine machine3 = new Machine("Charlie",3);
		Machine machine4 = new Machine(3);
		machine2.name();
		System.out.println(machine2.getName());
		machine2.setName("bobs");
		machine2.name();
		
		
		
}
	
	
	
	
}



