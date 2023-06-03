package ntlong.payload.response.categorytraining;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChildMenus {
    private long id;
    private String name;
    private int numOrder;
}
