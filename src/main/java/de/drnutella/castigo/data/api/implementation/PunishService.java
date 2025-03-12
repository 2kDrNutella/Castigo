package de.drnutella.castigo.data.api.implementation;

import de.drnutella.castigo.data.CacheManager;
import de.drnutella.castigo.data.api.dataAdapter.PunishDataAdapter;
import de.drnutella.castigo.enums.PunishFeedback;
import de.drnutella.castigo.enums.PunishRegion;
import de.drnutella.castigo.enums.PunishType;
import de.drnutella.castigo.objects.Punish;
import de.drnutella.castigo.objects.PunishInfoContainer;

import java.util.UUID;
import java.util.function.Consumer;

public class PunishService extends PunishDataAdapter {

    public static void punishPlayer(Punish punish, Consumer<PunishFeedback> callback){
        punishPlayerSQL(punish, callback::accept);
    }

    public static void unpunishPlayer(UUID uuid, PunishRegion punishRegion, Consumer<PunishFeedback> callback){
        unpunishPlayerSQL(uuid, punishRegion, callback::accept);
    }

    public static void getReasonCount(UUID uuid, String template, PunishRegion region, PunishType punishType, Consumer<Integer> callback){
        getReasonCountSQL(uuid, template, region, punishType, callback::accept);
    }

    public static void loadPunishInfoContainer(UUID uuid, Consumer<PunishInfoContainer> callback){
        if(!CacheManager.punishInfoContainerCache.containsKey(uuid)){
            loadPunishInfoContainerSQL(uuid, container -> {
                CacheManager.punishInfoContainerCache.put(uuid, container);
                callback.accept(container);
            });
        }else {
            callback.accept(CacheManager.punishInfoContainerCache.get(uuid));
        }
    }
}
