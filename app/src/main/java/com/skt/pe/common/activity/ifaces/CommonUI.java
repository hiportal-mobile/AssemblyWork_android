package com.skt.pe.common.activity.ifaces;

/*
 * Activity 구성 시 해당 코드를 다른 사람들이 봐도 이해하기가 쉽도록 UI에 관련된 설정의 일관성을 유지하기 위해 만든
 * 인터페이스
 * 해당 인터페이스로 구현된 Activity는 해당 인터페이스의 메소드는 클래스의 가장 하단에 위치시킨다.
 * by pluto248
 */
public interface CommonUI {
    /*
     * 최초 Activity 생성 시 화면 초기화 코드 생성
     */
    void initUI();

    /*
     * 화면이 초기화가 될 시 필요한 코드 생성
     */
    void resetUI();

    /*
     * 사용자 입력 항목이 있을 시 정합성을 체크하고 입력 데이터를 마무리하는 코드 생성
     * 정합성 오류가 있으면 오류 메시지를 리턴하고 오류가 없으면 null을 리턴
     */
    String validationUI();
}
