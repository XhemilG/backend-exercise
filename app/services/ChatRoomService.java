package services;

import com.google.inject.Inject;
import models.ChatAccessType;
import models.ChatRoom;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatRoomService {

    @Inject
    CRUDservice dbService;

    private static final String COLLECTION_NAME = "chat-rooms";

    public CompletableFuture<ChatAccessType> typeOfAccess(ObjectId roomId, List<ObjectId> objectIds) {
        return dbService.find(ChatRoom.class, "_id", roomId, COLLECTION_NAME)
                .thenApply(room -> {
                    if(room == null) {
                        return ChatAccessType.ROOM_NOT_FOUND;
                    }
                    if(room.getReadACL().isEmpty() && room.getWriteACL().isEmpty()) {
                        return ChatAccessType.WRITE;
                    }

                    boolean hasReadAccess = false;
                    boolean hasWriteAccess = false;

                    for(ObjectId id: objectIds) {
                        if(room.getReadACL().contains(id)) {
                            hasReadAccess = true;
                        }
                        if(room.getWriteACL().contains(id)) {
                            hasWriteAccess = true;
                            break;
                        }
                    }

                    if(hasWriteAccess) {
                        return ChatAccessType.WRITE;
                    }

                    if(hasReadAccess) {
                        return ChatAccessType.READ;
                    }

                    return ChatAccessType.NULL;
                });
    }
}
