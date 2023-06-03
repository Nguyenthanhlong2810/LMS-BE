package ntlong.payload.response.categorytraining;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MenuInternalResponse {
    private long id;
    private String name;
    private long parentId;
    private int numOrder;

    private List<MenuInternalResponse> childsMenu;

    public MenuInternalResponse(long id, String name, long parentId, int numOrder) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.numOrder = numOrder;
    }
}
