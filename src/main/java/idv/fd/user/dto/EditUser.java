package idv.fd.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditUser {

    private Long userId;

    @NotBlank(message = "Username is mandatory")
    @Size(max = 128)
    private String userName;

}
