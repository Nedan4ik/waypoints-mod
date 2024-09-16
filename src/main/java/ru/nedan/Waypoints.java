package ru.nedan;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nedan.functional.Rendering;
import ru.nedan.functional.WaypointsMap;

/**
 * @author nedan
 * discord nedan4ik
 * @since 16.09.2024
 */

@Mod("waypoints")
public class Waypoints {

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Инициализация главного класса
     */

    public Waypoints() {
        LOGGER.info("Загружается мод для вейпоинтов...");
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Rendering());
    }

    /**
     * Хук для регистра своей команды
     *
     * @param e - ивент для хука
     */

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent e) {
        LOGGER.info("Регистрирую команду...");

        e.getDispatcher().register(getCommand());

        LOGGER.info("Команда успешно зарегестрирована");
    }

    /**
     * Функция для получения команды клиента
     */

    private LiteralArgumentBuilder<CommandSource> getCommand() {
        return LiteralArgumentBuilder.<CommandSource>literal("waypoint")
                .then(Commands.literal("add")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                        .executes(context -> {
                                                            String name = StringArgumentType.getString(context, "name");
                                                            double x = DoubleArgumentType.getDouble(context, "x");
                                                            double y = DoubleArgumentType.getDouble(context, "y");
                                                            double z = DoubleArgumentType.getDouble(context, "z");

                                                            Pair<Boolean, String> pair = WaypointsMap.addWay(name, x, y, z);

                                                            IFormattableTextComponent component = new StringTextComponent("[Waypoints]").withStyle(TextFormatting.RED);
                                                            component.append(new StringTextComponent(": ").withStyle(TextFormatting.RESET));

                                                            if (!pair.getFirst()) {
                                                                component.append(new StringTextComponent("Ошибка!").withStyle(TextFormatting.DARK_RED));
                                                            } else {
                                                                component.append(new StringTextComponent("Успех!").withStyle(TextFormatting.GOLD));
                                                            }

                                                            component.append(" ");
                                                            component.append(new StringTextComponent(pair.getSecond()).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GREEN));

                                                            component.withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Нажмите, чтобы удалить"))).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoint remove " + name)));

                                                            context.getSource().sendSuccess(component, false);

                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");

                                    Pair<Boolean, String> pair = WaypointsMap.removeWay(name);

                                    IFormattableTextComponent component = new StringTextComponent("[Waypoints]").withStyle(TextFormatting.RED);
                                    component.append(new StringTextComponent(": ").withStyle(TextFormatting.RESET));

                                    if (!pair.getFirst()) {
                                        component.append(new StringTextComponent("Ошибка!").withStyle(TextFormatting.DARK_RED));
                                    } else {
                                        component.append(new StringTextComponent("Успех!").withStyle(TextFormatting.GOLD));
                                    }

                                    component.append(" ");
                                    component.append(new StringTextComponent(pair.getSecond()).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GREEN));
                                    context.getSource().sendSuccess(component, false);

                                    return 1;
                                })
                        )
                );
    }

}
