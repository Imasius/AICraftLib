package nimo.aic;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nimo.aic.grpc.*;

import java.util.concurrent.TimeUnit;

public class AICraft {

    private static final int DEFAULT_PORT = 40200;
    public static final char SEPARATOR = ':';

    private final ManagedChannel channel;
    private final AICraftGrpc.AICraftBlockingStub aiCraftBlocking;

    public AICraft() {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", DEFAULT_PORT).usePlaintext(true);
        channel = channelBuilder.build();
        aiCraftBlocking = AICraftGrpc.newBlockingStub(channel);
    }

    public void setName(String name, int x, int y, int z) {
        Position position = Position.newBuilder().setX(x).setY(y).setZ(z).build();
        SetNameRequest request = SetNameRequest.newBuilder().setPosition(position).setName(name).build();
        aiCraftBlocking.setName(request);
    }

    public void setId(String group, String id, int x, int y, int z) {
        Position position = Position.newBuilder().setX(x).setY(y).setZ(z).build();
        Id id1 = Id.newBuilder().setGroup(group).setId(id).build();
        SetIdRequest request = SetIdRequest.newBuilder().setId(id1).setPosition(position).build();
        aiCraftBlocking.setId(request);
    }

    public void transferItemStack(String from, int fromSlot, String to, int toSlot) {
        Id fromId = fromString(from);
        Id toId = fromString(to);
        TransferItemStackRequest request = TransferItemStackRequest.newBuilder().setFromId(fromId).setFromSlot(fromSlot)
                .setToId(toId).setToSlot(toSlot).build();
        aiCraftBlocking.transferItemStack(request);
    }

    public GetItemStackInfoResponse getItemStackInfo(String inventory, int slot) {
        Id id = fromString(inventory);
        GetItemStackInfoRequest request = GetItemStackInfoRequest.newBuilder().setId(id).setSlot(slot).build();
        return aiCraftBlocking.getItemStackInfo(request);
    }

    private Id fromString(String idString) {
        String[] idParts = idString.split(String.valueOf(SEPARATOR), 2);
        // TODO: Validation
        return Id.newBuilder().setGroup(idParts[0]).setId(idParts[1]).build();
    }

    public void shutdown() {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            /* do nothing */
        }
    }
}
