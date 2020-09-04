package com.hotmail.AdrianSR.BattleRoyale.iterator;

import java.util.List;
import java.util.ListIterator;

import com.hotmail.AdrianSR.BattleRoyale.game.Member;

/**
 * Represents a Area Iterator.
 * <p>
 * @author AdrianSR
 */
public class MemberIterator implements ListIterator<Member> {
	
	/**
	 * Class values.
	 */
    private final List<Member>  members;
    private       int         nextIndex;
    private       Boolean lastDirection;
    
    /**
     * Construct a new Area Iterator.
     * <p>
     * @param points the points list.
     */
    public MemberIterator(final List<Member> points) {
    	this.members    = points;
    	this.nextIndex = 0;
    }
    
	@Override
	public boolean hasNext() {
		return nextIndex < members.size();
	}

	@Override
	public Member next() {
		lastDirection = Boolean.TRUE;
		return members.get(nextIndex ++);
	}
	
	@Override
	public int nextIndex() {
		return nextIndex;
	}

	@Override
	public boolean hasPrevious() {
		return nextIndex > 0;
	}

	@Override
	public Member previous() {
		lastDirection = false;
		return members.get(-- nextIndex);
	}

	@Override
	public int previousIndex() {
		return nextIndex - 1;
	}
	
	@Override
	public void set(Member Member) {
		// check last direction
        if (lastDirection == null) {
            throw new IllegalStateException("No current Member!");
        }
        
        // set Member
        int i = lastDirection ? nextIndex - 1 : nextIndex;
        members.set(i, Member);
	}
	
	@Override
	public void add(Member Member) {
		throw new UnsupportedOperationException("Cannot change the size of a member list!");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot change the size of a member list!");
	}
}
