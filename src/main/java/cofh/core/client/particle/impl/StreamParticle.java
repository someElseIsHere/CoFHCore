package cofh.core.client.particle.impl;

import cofh.core.client.particle.PointToPointParticle;
import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class StreamParticle extends PointToPointParticle {

    protected Vec3 dest;
    protected Vec3[] path;
    protected List<Vector3f> curve;

    private StreamParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
        dest = new Vec3(ex, ey, ez);
        path = findPath();
        curve = new ArrayList<>(path.length * 4);
    }

    @Override
    public void tick() {

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        if (c0.a <= 0) {
            return;
        }
        int total = path.length * 4;
        float inv = 1.0F / total;
        float end = time * (total - 1) / this.duration;
        int ceil = MathHelper.clamp(MathHelper.ceil(end), 1, total - 1);
        for (int i = curve.size(); i <= ceil; ++i) {
            Vec3 b = bezier(i * inv);
            curve.add(new Vector3f((float) (b.x - x), (float) (b.y - y), (float) (b.z - z)));
        }
        float start = Math.max(0, end - size * 4);
        int floor = MathHelper.floor(start);
        Vector4f[] posns = new Vector4f[ceil - floor + 1];
        for (int i = floor; i <= ceil; ++i) {
            posns[i - floor] = MathHelper.toVector4f(curve.get(i));
        }
        posns[0].lerp(MathHelper.toVector4f(curve.get(floor + 1)), start - floor);
        float offset = ceil - end;
        posns[posns.length - 1].lerp(MathHelper.toVector4f(curve.get(ceil - 1)), offset);

        if (posns.length < 2) {
            return;
        }

        Matrix4f pose = stack.last().pose();
        Vector3f normal = VFXHelper.normal(stack);

        for (Vector4f pos : posns) {
            pos.mul(pose);
        }
        int last = posns.length - 1;
        VFXHelper.VFXNode[] nodes = new VFXHelper.VFXNode[posns.length];
        inv = 1.0F / posns.length;
        for (int i = 1; i < last; ++i) {
            float width = (MathHelper.sin(i + offset) * 0.1F + 0.2F) * MathHelper.easePlateau(inv);
            nodes[i] = new VFXHelper.VFXNode(posns[i], VFXHelper.axialPerp(posns[i - 1], posns[i + 1], width), width);
        }
        float width = 0;
        nodes[0] = new VFXHelper.VFXNode(posns[0], VFXHelper.axialPerp(posns[0], posns[1], width), width);
        width = 0;
        nodes[last] = new VFXHelper.VFXNode(posns[last], VFXHelper.axialPerp(posns[last - 1], posns[last], width), width);
        VFXHelper.renderNodes(normal, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, nodes, c0);


        //float len = end - start;
        //Function<Float, Float> width = p -> 0.2F * (MathHelper.sin(len * p) * 0.5F + 1.0F) * MathHelper.easePlateau(p);
        //
        //VFXHelper.renderStreamLine(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, poss, c0, width);
    }

    // region PATHFINDING
    protected Vec3 bezier(float t) {

        Vec3[] pts = path;
        while (pts.length > 1) {
            Vec3[] next = new Vec3[pts.length - 1];
            for (int j = 0; j < next.length; ++j) {
                next[j] = pts[j].lerp(pts[j + 1], t);
            }
            pts = next;
        }
        return pts[0];
    }

    protected List<Direction> successorOrder() {
        // TODO
        return List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN);
    }

    protected Vec3[] findPath() {

        BlockPos start = BlockPos.containing(x, y, z);
        BlockPos end = BlockPos.containing(dest);
        List<Direction> order = successorOrder();
        int capacity = distManhattan(start, end) * 2;
        PriorityQueue<Node> open = new ObjectHeapPriorityQueue<>(capacity, Comparator.comparingInt(n -> n.total + n.heuristic));
        Map<BlockPos, Node> nodes = new Object2ObjectOpenHashMap<>(capacity);
        Node first = new Node(start, distManhattan(start, end));
        open.enqueue(first);
        nodes.put(start, first);

        while (!open.isEmpty()) {
            Node node = open.dequeue();
            if (!node.inOpen) {
                continue;
            }
            for (Direction dir : order) {
                BlockPos pos = node.pos.relative(dir);
                if (pos.equals(end)) {
                    return new Node(node, pos, 0, 0).toPath();
                }
                Node successor = nodes.get(pos);
                if (successor == null) {
                    int cost = level.isEmptyBlock(pos) ? 1 : 6;
                    successor = new Node(node, pos, cost, distManhattan(pos, end));
                } else if (successor.inOpen) {
                    int total = successor.cost + node.total;
                    if (total >= successor.total) {
                        continue;
                    }
                    successor.inOpen = false;
                    successor = successor.copy(node, total);
                } else {
                    int total = successor.cost + node.total;
                    if (total >= successor.total) {
                        continue;
                    }
                    successor = successor.copy(node, total);
                }
                nodes.put(pos, successor);
                open.enqueue(successor);
            }
        }
        return new Vec3[]{};
    }

    // Impl without random float cast.
    protected int distManhattan(BlockPos a, BlockPos b) {

        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    protected static class Node {

        public final Node parent;
        public final int total;
        public final BlockPos pos;
        public final int cost;
        public final int heuristic;
        public boolean inOpen;

        private Node(Node parent, BlockPos pos, int cost, int total, int heuristic, boolean inOpen) {

            this.parent = parent;
            this.total = total;
            this.pos = pos;
            this.cost = cost;
            this.heuristic = heuristic;
            this.inOpen = inOpen;
        }

        public Node(BlockPos pos, int heuristic) {

            this(null, pos, 0, 0, heuristic, true);
        }

        public Node(Node parent, BlockPos pos, int cost, int heuristic) {

            this(parent, pos, cost, parent.total + cost, heuristic, true);
        }

        public boolean equals(Node other) {

            return this.pos.equals(other.pos);
        }

        public Node copy(Node parent, int total) {

            return new Node(parent, pos, cost, total, heuristic, true);
        }

        public Vec3[] toPath() {

            return Lists.reverse(Stream.iterate(this, n -> n.parent != null, n -> n.parent).map(n -> n.pos).map(Vec3::atCenterOf).toList()).toArray(Vec3[]::new);
        }

    }
    // endregion

    @Nonnull
    public static ParticleProvider<BiColorParticleOptions> factory(SpriteSet spriteSet) {

        return StreamParticle::new;
    }

}
