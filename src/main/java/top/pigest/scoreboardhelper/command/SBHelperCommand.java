/*
 * The MIT License
 *
 * Copyright (c) 2024 TmallKing1
 * Copyright (c) 2026 EnderPhantomWing
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package top.pigest.scoreboardhelper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import top.pigest.scoreboardhelper.config.ScoreboardHelperConfig;

public class SBHelperCommand {
    private static final SimpleCommandExceptionType INVALID_COUNT_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.sbhelper.invalidCount"));

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("sbhelper")
                .then(ClientCommandManager.literal("maxDisplayCount")
                        .then(ClientCommandManager.argument("count", IntegerArgumentType.integer())
                                .executes(context -> executeMaxDisplayCount(context.getSource(), IntegerArgumentType.getInteger(context, "count")))))
        );

    }

    private static int executeMaxDisplayCount(FabricClientCommandSource source, int count) throws CommandSyntaxException {
        if(count < 0) {
            throw INVALID_COUNT_EXCEPTION.create();
        }
        ScoreboardHelperConfig.INSTANCE.maxShowCount.setValue(count);
        source.sendFeedback(Component.translatable("commands.sbhelper.success.setMaxCount", count));
        return count;
    }
}
