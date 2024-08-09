package vn.hti.sf.testsearch.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ResponseDto<T> implements Serializable {

    String message;
    String code;
    T data;

    public ResponseDto(T data) {
        this.message = "Thành công";
        this.code = "200";
        this.data = data;
    }

}
