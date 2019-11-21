package tw.edu.bircdemo.service.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.edu.bircdemo.bean.MemberBean;
import tw.edu.bircdemo.dao.MemberDAO;
import tw.edu.bircdemo.service.MemberService;
import tw.edu.bircdemo.vo.Member;

@Service
public class MemberServiceImplement extends BaseServiceImpl<MemberDAO, Member, MemberBean> implements MemberService{
	private MemberDAO memberDAO;
	
	@Autowired
	public MemberServiceImplement(MemberDAO baseDAO) {
		super(baseDAO);
		this.memberDAO = baseDAO;
	}

	@Transactional
	@Override
	public MemberBean createAndReturn(MemberBean bean) throws RuntimeException {
		// Controller > Service > DAO > DB
		//  		Bean		VO 	
		
		Member member = createVO(bean);
		int id = (int) memberDAO.insertAndReturn(member);
		bean.setId(id);
		return bean;
	}

	@Override
	protected Member createVO(MemberBean bean) {
		Member member = new Member();
		member.setId(bean.getId());
		member.setFirstName(bean.getFirstName());
		member.setLastName(bean.getLastName());
		member.setEmail(bean.getEmail());
		member.setGender(bean.getGender());
		return member;
	}

	@Override
	protected MemberBean createBean(Member entity) {
		MemberBean memberBean = new MemberBean();
		memberBean.setId(entity.getId());
		memberBean.setFirstName(entity.getFirstName());
		memberBean.setLastName(entity.getLastName());
		memberBean.setEmail(entity.getEmail());
		memberBean.setGender(entity.getGender());
		return memberBean;
		
	}

}
