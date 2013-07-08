////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package iwa_yuki.touhou_alice_dolls;

public class DollRegistry
{
    private static String[] dollNameList = new String[256];

    public static void addDoll(int id, String name)
    {
        if (dollNameList[id] != null)
        {
            System.out.println("CONFLICT @ " + id +
                               " item slot already occupied by " +
                               dollNameList[id] + " while adding " + name);
        }

        dollNameList[id] = name;
    }
}
