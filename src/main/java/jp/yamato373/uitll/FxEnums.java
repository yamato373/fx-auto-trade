package jp.yamato373.uitll;


public class FxEnums {

	public static enum Status{

		BEFORE(0, "注文前", 'B'),
		ORDER(1, "注文中", '0'),
		FILL(2, "約定", '2'),
		REJECT(3, "リジェクト", '8')
	    ;

		private final int code;
	    private final String text;
	    private final char fieldCode;

	    private Status(final int code, final String text, final char fieldCode) {
	    	this.code = code;
	        this.text = text;
	        this.fieldCode = fieldCode;
	    }

	    public int getCode() {
	        return this.code;
	    }

	    public String getText() {
	        return this.text;
	    }

	    public char getFieldCode() {
	        return this.fieldCode;
	    }
	}

	public static Status getStatus(final int code) {
		Status[] statuses = Status.values();
        for (Status status : statuses) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

	public static Status getStatus(final char fieldCode) {
		Status[] statuses = Status.values();
        for (Status status : statuses) {
            if (status.getFieldCode() == fieldCode) {
                return status;
            }
        }
        return null;
    }

	public static enum Side{

		BID(0, "売り", '1'),
		ASK(1, "買い", '2'),
	    ;

		private final int code;
	    private final String text;
	    private final char fieldCode;

	    private Side(final int code, final String text, final char fieldCode) {
	    	this.code = code;
	        this.text = text;
	        this.fieldCode = fieldCode;
	    }

	    public int getCode() {
	        return this.code;
	    }

	    public String getText() {
	        return this.text;
	    }

	    public char getFieldCode() {
	        return this.fieldCode;
	    }
	}

    public static Side getSide(final int code) {
    	Side[] sides = Side.values();
        for (Side side : sides) {
            if (side.getCode() == code) {
                return side;
            }
        }
        return null;
    }
}
